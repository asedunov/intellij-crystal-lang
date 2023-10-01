package org.crystal.intellij.lang.ast

import com.intellij.openapi.project.Project
import org.crystal.intellij.lang.ast.nodes.*
import org.crystal.intellij.lang.resolve.CrResolveFacade
import org.crystal.intellij.lang.resolve.resolveFacade

class CstLiteralNamedExpander(resolveFacade: CrResolveFacade) : CstLiteralExpanderBase(resolveFacade) {
    override fun transformArrayLiteral(o: CstArrayLiteral): CstNode<*> {
        return transformArrayLiteral(o, null)
    }

    fun transformArrayLiteral(o: CstArrayLiteral, genericType: CstNode<*>?): CstNode<*> {
        val loc = o.location
        val elements = o.elements

        val elemTempVars = complexElementsTempVars(elements)
        val receiver = when {
            genericType != null -> {
                val typeOf = typeOfExp(o, elemTempVars)
                CstGeneric(genericType, listOf(typeOf), location = loc)
            }

            else -> o.receiverType
        }

        val constructor = CstCall(receiver, "new", location = loc)
        if (elements.isEmpty()) return constructor

        val aryVar = resolveCache.newTempVar(loc)

        val exps = ArrayList<CstNode<*>>(elements.size + elemTempVars.size + 2)

        elemTempVars.keys.iterator().forEachRemaining { i ->
            val elemTempVar = elemTempVars[i]
            var elemExp = elements[i]
            if (elemExp is CstSplat) elemExp = elemExp.expression
            exps += CstAssign(elemTempVar, elemExp, location = elemTempVar.location)
        }
        exps += CstAssign(aryVar, constructor, loc)

        for ((i, elem) in elements.withIndex()) {
            val tempVar = elemTempVars[i]
            if (elem is CstSplat) {
                val yieldVar = resolveCache.newTempVar()
                val eachBody = CstCall(aryVar, "<<", yieldVar, location = loc)
                val eachBlock = CstBlock(args = listOf(yieldVar), body = eachBody, location = loc)
                exps += CstCall(
                    obj = tempVar ?: elem.expression,
                    name = "each",
                    block = eachBlock,
                    location = loc
                )
            }
            else {
                exps += CstCall(
                    obj = aryVar,
                    name = "<<",
                    arg = tempVar ?: elem,
                    location = loc
                )
            }
        }

        exps += aryVar

        return CstExpressions(exps, location = loc)
    }

    override fun transformHashLiteral(o: CstHashLiteral): CstNode<*> {
        return transformHashLiteral(o, null)
    }

    fun transformHashLiteral(o: CstHashLiteral, genericType: CstNode<*>?): CstNode<*> {
        val loc = o.location
        val entries = o.entries

        val keyTempVars = complexElementsTempVars(entries) { it.key }
        val valueTempVars = complexElementsTempVars(entries) { it.value }

        val of = o.elementType
        val generic = when {
            of != null -> {
                val typeVars = listOf(of.key, of.value)
                CstGeneric(CstPath.global("Hash"), typeVars, location = loc)
            }

            genericType != null -> {
                val typeOfKey = CstTypeOf(
                    entries.mapIndexed { i, x -> keyTempVars[i] ?: x.key }
                )
                val typeOfValue = CstTypeOf(
                    entries.mapIndexed { i, x -> valueTempVars[i] ?: x.value }
                )
                CstGeneric(genericType, listOf(typeOfKey, typeOfValue), location = loc)
            }

            else -> {
                o.receiverType
            }
        }

        val constructor = CstCall(generic, "new", location = loc)
        if (entries.isEmpty()) return constructor

        val hashVar = resolveCache.newTempVar()

        val exps = ArrayList<CstNode<*>>(entries.size + keyTempVars.size + valueTempVars.size + 2)
        keyTempVars.keys.iterator().forEachRemaining { i ->
            val keyTempVar = keyTempVars[i]
            val keyExp = entries[i].key
            exps += CstAssign(keyTempVar, keyExp, keyTempVar.location)
        }
        valueTempVars.keys.iterator().forEachRemaining { i ->
            val valueTempVar = valueTempVars[i]
            val valueExp = entries[i].value
            exps += CstAssign(valueTempVar, valueExp, valueTempVar.location)
        }
        exps += CstAssign(hashVar, constructor, loc)

        for ((i, entry) in entries.withIndex()) {
            val keyExp = keyTempVars[i] ?: entry.key
            val valueExp = valueTempVars[i] ?: entry.value
            exps += CstCall(
                obj = hashVar,
                name = "[]=",
                args = listOf(keyExp, valueExp),
                location = loc
            )
        }

        exps += hashVar

        return CstExpressions(exps, location = loc)
    }
}

fun CstNode<*>.expandLiteralNamed(
    project: Project,
    genericType: CstNode<*>? = null
): CstNode<*> {
    val expander = project.resolveFacade.literalNamedExpander
    return when (this) {
        is CstArrayLiteral -> expander.transformArrayLiteral(this, genericType)
        is CstHashLiteral -> expander.transformHashLiteral(this, genericType)
        else -> expander.transform(this)
    }
}
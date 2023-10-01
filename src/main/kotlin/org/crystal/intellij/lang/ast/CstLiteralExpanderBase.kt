package org.crystal.intellij.lang.ast

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.crystal.intellij.lang.ast.nodes.*
import org.crystal.intellij.lang.resolve.CrResolveFacade

abstract class CstLiteralExpanderBase(protected val resolveFacade: CrResolveFacade) : CstTransformer() {
    protected val resolveCache
        get() = resolveFacade.resolveCache

    protected fun complexElementsTempVars(
        elements: List<CstNode<*>>
    ): Int2ObjectMap<CstVar> = complexElementsTempVars(elements) { it }

    protected fun <T> complexElementsTempVars(
        elements: List<T>,
        node: (T) -> CstNode<*>
    ): Int2ObjectMap<CstVar> {
        val tempVars = Int2ObjectOpenHashMap<CstVar>()
        for((i, e) in elements.withIndex()) {
            var elem = node(e)
            if (elem is CstSplat) elem = elem.expression
            if (elem is CstVar || elem is CstInstanceVar || elem is CstClassVar || elem is CstSimpleLiteral) continue
            tempVars.put(i, resolveCache.newTempVar(elem.location))
        }
        return tempVars
    }

    protected fun typeOfExp(node: CstArrayLiteral, tempVars: Int2ObjectMap<CstVar>): CstTypeOf {
        val loc = node.location
        val typeExps = node.elements.mapIndexed { i: Int, elem: CstNode<*> ->
            val tempVar = tempVars[i]
            if (elem is CstSplat) {
                CstCall(
                    obj = CstPath.global("Enumerable", loc),
                    name = "element_type",
                    arg = tempVar ?: elem.expression,
                    location = loc
                )
            }
            else {
                tempVar ?: elem
            }
        }
        return CstTypeOf(typeExps, loc)
    }
}
package org.crystal.intellij.lang.ast

import com.intellij.openapi.project.Project
import com.intellij.util.SmartList
import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.nodes.*
import org.crystal.intellij.lang.config.CrystalFlags
import org.crystal.intellij.lang.config.hasCrystalFlag
import org.crystal.intellij.lang.resolve.CrResolveFacade
import org.crystal.intellij.lang.resolve.resolveFacade
import org.crystal.intellij.util.cast
import org.crystal.intellij.util.crGet

class CstLiteralExpander(resolveFacade: CrResolveFacade) : CstLiteralExpanderBase(resolveFacade) {
    override fun transformAnd(o: CstAnd): CstNode<*> {
        val left = o.left.singleExpression
        val right = o.right
        return when {
            left is CstVar || left is CstIsA && left.obj is CstVar -> {
                CstIf(left, right, left, location = o.location)
            }

            left is CstAssign && left.target is CstVar -> {
                CstIf(left, right, left.target, location = o.location)
            }

            left is CstNot && left.expression is CstVar -> {
                CstIf(left, right, left, location = o.location)
            }

            left is CstNot && (left.expression as? CstIsA)?.obj is CstVar -> {
                CstIf(left, right, left, location = o.location)
            }

            else -> {
                val tempVar = resolveCache.newTempVar()
                CstIf(
                    CstAssign(tempVar, left, o.location),
                    right,
                    tempVar,
                    location = o.location
                )
            }
        }
    }

    override fun transformOr(o: CstOr): CstNode<*> {
        val left = o.left.singleExpression
        val right = o.right
        return when {
            left is CstVar || left is CstIsA && left.obj is CstVar -> {
                CstIf(left, left, right, location = o.location)
            }

            left is CstAssign && left.target is CstVar -> {
                CstIf(left, left.target, right, location = o.location)
            }

            left is CstNot && left.expression is CstVar -> {
                CstIf(left, left, right, location = o.location)
            }

            left is CstNot && (left.expression as? CstIsA)?.obj is CstVar -> {
                CstIf(left, left, right, location = o.location)
            }

            else -> {
                val tempVar = resolveCache.newTempVar()
                CstIf(
                    CstAssign(tempVar, left, o.location),
                    tempVar,
                    right,
                    location = o.location
                )
            }
        }
    }

    override fun transformRangeLiteral(o: CstRangeLiteral): CstNode<*> {
        val path = CstPath.global("Range", o.location)
        val bool = CstBoolLiteral.of(o.isExclusive, o.location)
        return CstCall(path, "new", listOf(o.from, o.to, bool), location = o.location)
    }

    override fun transformProcPointer(o: CstProcPointer): CstNode<*> {
        val obj = o.obj

        val assign: CstAssign?
        val newObj: CstNode<*>?
        if (obj != null && obj !is CstPath) {
            val tempVar = resolveCache.newTempVar(obj.location)
            assign = CstAssign(tempVar, obj)
            newObj = tempVar
        }
        else {
            assign = null
            newObj = obj
        }

        val defArgs = o.args.map { arg ->
            CstArg(
                name = resolveCache.newTempVarName(),
                restriction = arg,
                location = arg.location
            )
        }

        val callArgs = defArgs.map { defArg ->
            CstVar(defArg.name, defArg.location)
        }

        val body = CstCall(newObj, o.name, callArgs, isGlobal = o.isGlobal, location = o.location)
        val procLiteral = CstProcLiteral(CstDef("->", defArgs, body, location = o.location))

        return if (assign != null) CstExpressions(listOf(assign, procLiteral)) else procLiteral
    }

    override fun transformStringInterpolation(o: CstStringInterpolation): CstNode<*> {
        return CstCall(
            obj = CstPath.global("String", o.location),
            name = "interpolation",
            args = o.combineContiguousStringLiterals(),
            location = o.location
        )
    }

    private fun CstStringInterpolation.combineContiguousStringLiterals(): List<CstNode<*>> {
        val newExpressions = SmartList<CstNode<*>>()
        val sb = StringBuilder()
        for (expression in expressions) {
            when (expression) {
                is CstStringLiteral -> sb.append(expression.value)
                else -> {
                    if (sb.isNotEmpty()) {
                        newExpressions += CstStringLiteral(sb.toString())
                        sb.clear()
                    }
                    newExpressions += expression
                }
            }
        }
        if (sb.isNotEmpty()) {
            newExpressions += CstStringLiteral(sb.toString())
        }
        return newExpressions
    }

    override fun transformRegexLiteral(o: CstRegexLiteral): CstNode<*> {
        return when (o.source) {
            is CstStringLiteral -> {
                val constName = resolveCache.newRegexConstName()
                CstPath(listOf(constName), location = o.location)
            }

            else -> regexNewCall(o, o.source)
        }
    }

    private fun regexNewCall(o: CstRegexLiteral, value: CstNode<*>): CstNode<*> {
        val loc = o.location
        return CstCall(
            obj = CstPath.global("Regex", loc),
            name = "new",
            args = listOf(value, regexOptions(o)),
            location = loc
        )
    }

    private fun regexOptions(o: CstRegexLiteral): CstNode<*> {
        val loc = o.location
        return CstCall(
            obj = CstPath.global(listOf("Regex", "Options"), loc),
            name = "new",
            arg = CstNumberLiteral(o.options, location = loc),
            location = loc
        )
    }

    override fun transformCase(o: CstCase): CstNode<*> {
        val nodeCond = o.condition

        if (o.whenBranches.isEmpty()) {
            val expressions = SmartList<CstNode<*>>()
            val elseBranch = o.elseBranch
            if (nodeCond != null) {
                expressions += nodeCond
                if (elseBranch == null) expressions += CstNilLiteral.EMPTY
            }
            if (elseBranch != null) {
                expressions += elseBranch
            }
            return CstExpressions(expressions, location = o.location)
        }

        val assigns = SmartList<CstNode<*>>()
        val conditionRefs = SmartList<CstNode<*>>()

        if (nodeCond != null) {
            val conditions = (nodeCond as? CstTupleLiteral)?.elements ?: listOf(nodeCond)
            conditions.forEach {
                when (val cond = it.singleExpression) {
                    is CstVar, is CstInstanceVar -> {
                        conditionRefs += cond
                    }

                    is CstAssign -> {
                        conditionRefs += cond.target
                        assigns += cond
                    }

                    else -> {
                        val tempVar = resolveCache.newTempVar()
                        conditionRefs += tempVar
                        assigns += CstAssign(tempVar, cond, cond.location)
                    }
                }
            }
        }

        val effectiveElse = if (o.isExhaustive) o.elseBranch ?: CstUnreachable else o.elseBranch
        val caseIf = o.whenBranches.foldRight(effectiveElse) { wh, acc ->
            val comparison = whenToFullComparison(wh, nodeCond, conditionRefs)
            CstIf(comparison, wh.body, acc ?: CstNop, location = comparison.location)
        } ?: CstNop

        if (assigns.isEmpty()) return caseIf

        assigns += caseIf
        return CstExpressions(assigns, location = o.location)
    }

    private fun whenToFullComparison(
        wh: CstWhen,
        nodeCond: CstNode<*>?,
        conditionRefs: SmartList<CstNode<*>>
    ) = wh.conditions.fold(null as CstNode<*>?) { acc, cond ->
        if (cond is CstUnderscore) return@fold acc

        val comparison = whenConditionToFullComparison(nodeCond, cond, conditionRefs) ?: return@fold acc

        if (acc != null) {
            CstOr(acc, comparison, acc.location)
        } else {
            comparison
        }
    } ?: CstBoolLiteral.TRUE

    private fun whenConditionToFullComparison(
        nodeCond: CstNode<*>?,
        cond: CstNode<*>,
        conditionRefs: SmartList<CstNode<*>>
    ): CstNode<*>? {
        if (nodeCond !is CstTupleLiteral) {
            val tempVar = conditionRefs.firstOrNull()
            return whenConditionToComparison(tempVar, cond, cond.location)
        }

        if (cond !is CstTupleLiteral) {
            return whenConditionToComparison(CstTupleLiteral(conditionRefs), cond, cond.location)
        }

        return cond.elements.zip(conditionRefs).fold(null as CstNode<*>?) { acc, (lh, rh) ->
            if (lh is CstUnderscore) return@fold acc
            val subComparison = whenConditionToComparison(rh, lh, cond.location)
            if (acc != null) {
                CstAnd(acc, subComparison, acc.location)
            } else {
                subComparison
            }
        }
    }

    private fun whenConditionToComparison(conditionRef: CstNode<*>?, cond: CstNode<*>, location: CstLocation?): CstNode<*> {
        if (conditionRef == null) return cond

        checkImplicitObj(cond, conditionRef, location)?.let { return it }

        when (cond) {
            is CstNilLiteral -> return CstIsA(conditionRef, CstPath.global("Nil"), location = location)
            is CstPath, is CstGeneric -> return CstIsA(conditionRef, cond, location = location)
            is CstCall -> {
                when (val obj = cond.obj) {
                    is CstPath, is CstGeneric -> {
                        if (cond.name == "class") return CstIsA(conditionRef, CstMetaclass(obj, obj.location), location = location)
                    }
                    else -> {}
                }
            }
            else -> {}
        }

        return CstCall(cond, "===", conditionRef, location = location)
    }

    private fun checkImplicitObj(
        cond: CstNode<*>,
        tempVar: CstNode<*>,
        location: CstLocation?
    ): CstNode<*>? {
        val condObj = when (cond) {
            is CstNot -> cond.expression
            is CstNodeWithReceiver<*> -> cond.obj
            else -> return null
        }
        if (condObj !is CstImplicitObj) return null
        return when (cond) {
            is CstNot -> cond.copy(expression = tempVar, location = location)
            is CstNodeWithReceiver<*> -> cond.withReceiver(tempVar).withLocation(location)
            else -> null
        }
    }

    override fun transformSelect(o: CstSelect): CstNode<*> {
        val loc = o.location
        val indexVar = resolveCache.newTempVar(loc)
        val valueVar = resolveCache.newTempVar(loc)

        val targets = listOf(indexVar, valueVar)
        val channel = CstPath.global("Channel", loc)

        val tupleValues = SmartList<CstNode<*>>()
        val caseWhens = SmartList<CstWhen>()

        for ((i, wh) in o.whens.withIndex()) {
            when (val condition = wh.condition) {
                is CstCall -> {
                    val newCall = condition.copy(name = selectActionName(condition.name))
                    tupleValues += newCall
                    caseWhens += CstWhen(
                        listOf(CstNumberLiteral(i, location = loc)),
                        wh.body
                    )
                }

                is CstAssign -> {
                    val call = condition.value as? CstCall ?: continue
                    val newCall = call.copy(name = selectActionName(call.name))
                    tupleValues += newCall
                    val typeOf = CstTypeOf(listOf(call), loc)
                    val cast = CstCast(valueVar, typeOf, loc)
                    val newAssign = CstAssign(condition.target, cast, loc)
                    val newBody = CstExpressions(listOf(newAssign, wh.body))
                    caseWhens += CstWhen(
                        listOf(CstNumberLiteral(i, location = loc)),
                        newBody
                    )
                }

                else -> {}
            }
        }

        val caseElse = o.elseBranch ?: CstCall(
            obj = null,
            name = "raise",
            arg = CstStringLiteral("BUG: invalid select index"),
            isGlobal = true,
            location = loc
        )

        val call = CstCall(
            obj = channel,
            name = if (o.elseBranch != null) "non_blocking_select" else "select",
            arg = CstTupleLiteral(tupleValues, loc),
            location = loc
        )
        val multiAssign = CstMultiAssign(targets, listOf(call))
        val case = CstCase(indexVar, caseWhens, caseElse, false, loc)
        return CstExpressions(listOf(multiAssign, case), location = loc)
    }

    private fun selectActionName(name: String) = when {
        name.endsWith('!') -> name.substring(0, name.lastIndex) + "_select_action!"
        name.endsWith('?') -> name.substring(0, name.lastIndex) + "_select_action?"
        else -> name + "_select_action"
    }

    override fun transformArrayLiteral(o: CstArrayLiteral): CstNode<*> {
        val loc = o.location
        val elements = o.elements

        val elemTempVars = complexElementsTempVars(elements)
        val typeVar = o.elementType ?: typeOfExp(o, elemTempVars)

        val capacity = elements.count { it !is CstSplat }

        val generic = CstGeneric(CstPath.global("Array"), listOf(typeVar), location = loc)

        return when {
            elements.any { it is CstSplat } -> {
                val aryVar = resolveCache.newTempVar(loc)

                val aryInstance = CstCall(
                    obj = generic,
                    name = "new",
                    arg = CstNumberLiteral(capacity, location = loc),
                    location = loc
                )

                val exps = ArrayList<CstNode<*>>(elements.size + elemTempVars.size + 2)
                for (entry in elemTempVars.int2ObjectEntrySet()) {
                    val i = entry.intKey
                    val elemTempVar = entry.value
                    var elemExp = elements[i]
                    if (elemExp is CstSplat) elemExp = elemExp.expression
                    exps += CstAssign(elemTempVar, elemExp, elemTempVar.location)
                }
                exps += CstAssign(aryVar, aryInstance, loc)

                for ((i, elem) in elements.withIndex()) {
                    val tempVar = elemTempVars[i]
                    if (elem is CstSplat) {
                        exps += CstCall(
                            obj = aryVar,
                            name = "concat",
                            arg = tempVar ?: elem.expression,
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

                CstExpressions(exps, location = loc)
            }

            capacity == 0 -> {
                CstCall(
                    obj = generic,
                    name = "new",
                    location = loc
                )
            }

            else -> {
                val aryVar = resolveCache.newTempVar(loc)

                val aryInstance = CstCall(
                    generic,
                    "unsafe_build",
                    CstNumberLiteral(capacity, location = loc),
                    location = loc
                )

                val buffer = CstCall(
                    aryVar,
                    "to_unsafe",
                    location = loc
                )
                val bufferVar = resolveCache.newTempVar(loc)

                val exps = ArrayList<CstNode<*>>(elements.size + elemTempVars.size + 3)
                for (entry in elemTempVars.int2ObjectEntrySet()) {
                    val i = entry.intKey
                    val elemTempVar = entry.value
                    val elemExp = elements[i]
                    exps += CstAssign(elemTempVar, elemExp, elemTempVar.location)
                }
                exps += CstAssign(aryVar, aryInstance, loc)
                exps += CstAssign(bufferVar, buffer, loc)

                for ((i, elem) in elements.withIndex()) {
                    val tempVar = elemTempVars[i]
                    exps += CstCall(
                        obj = bufferVar,
                        name = "[]=",
                        args = listOf(CstNumberLiteral(i, location = loc), tempVar ?: elem),
                        location = loc
                    )
                }

                exps += aryVar

                CstExpressions(exps, location = loc)
            }
        }
    }

    override fun transformHashLiteral(o: CstHashLiteral): CstNode<*> {
        return resolveFacade.literalNamedExpander.transformHashLiteral(o, CstPath.global("Hash"))
    }

    override fun transformMultiAssign(o: CstMultiAssign): CstNode<*> {
        val targets = o.targets
        val values = o.values

        val splatIndex = targets.indexOfLast { it is CstSplat }
        val isUnderscoreSplat = splatIndex >= 0 && targets[splatIndex].cast<CstSplat>().expression is CstUnderscore

        val singleValue = values.singleOrNull()
        return if (singleValue != null) {
            transformMultiAssignWithSingleRHS(singleValue, splatIndex, targets, isUnderscoreSplat, o)
        }
        else {
            if (splatIndex >= 0) {
                if (targets.size - 1 > values.size) return o
            }
            else {
                if (targets.size != values.size) return o
            }

            val loc = o.location

            val assignToCount = if (isUnderscoreSplat) values.size else targets.size
            val assignFromCount = targets.size - (if (isUnderscoreSplat) 1 else 0)
            val assignToTemps = ArrayList<CstNode<*>>(assignToCount)
            val assignFromTemps = ArrayList<CstNode<*>>(assignFromCount)

            for ((i, target) in targets.withIndex()) {
                val value = if (i == splatIndex) {
                    if (isUnderscoreSplat) {
                        assignToTemps += values.crGet(i .. i - targets.size)
                        continue
                    }
                    CstCall(
                        CstPath.global("Tuple", location = loc),
                        "new",
                        values.crGet(i .. i - targets.size)
                    )
                }
                else {
                    values.crGet(if (splatIndex in 0..<i) i - targets.size else i)
                }

                val tempVar = resolveCache.newTempVar()
                assignToTemps += CstAssign(tempVar, value, loc)
                assignFromTemps += transformMultiAssignTarget(target, tempVar)
            }

            CstExpressions(assignToTemps + assignFromTemps, location = loc)
        }
    }

    private fun transformMultiAssignWithSingleRHS(
        singleValue: CstNode<*>,
        splatIndex: Int,
        targets: List<CstNode<*>>,
        isUnderscoreSplat: Boolean,
        o: CstMultiAssign
    ): CstExpressions {
        val valueLoc = singleValue.location

        val isMiddleSplat = splatIndex > 0 && splatIndex < targets.lastIndex
        val raiseOnCountMismatch =
            isMiddleSplat || resolveFacade.program.project.hasCrystalFlag(CrystalFlags.STRICT_MULTI_ASSIGN)

        val tempVar = resolveCache.newTempVar()

        val assigns = ArrayList<CstNode<*>>(
            targets.size + (if (isUnderscoreSplat) 1 else 0) + (if (raiseOnCountMismatch) 1 else 0)
        )
        assigns += CstAssign(tempVar, singleValue, valueLoc)

        if (raiseOnCountMismatch) {
            val sizeCall = CstCall(tempVar, "size", location = valueLoc)
            val sizeComp = if (isMiddleSplat) {
                CstCall(sizeCall, "<", CstNumberLiteral(targets.lastIndex), location = valueLoc)
            } else {
                CstCall(sizeCall, "!=", CstNumberLiteral(targets.size), location = valueLoc)
            }
            val indexError = CstCall(
                obj = CstPath.global("IndexError"),
                name = "new",
                arg = CstStringLiteral("Multiple assignment count mismatch"),
                location = valueLoc
            )
            val raiseCall = CstCall.global("raise", indexError, valueLoc)
            assigns += CstIf(sizeComp, raiseCall, location = valueLoc)
        }

        for ((i, target) in targets.withIndex()) {
            val indexer = if (i == splatIndex) {
                if (isUnderscoreSplat) continue
                CstRangeLiteral(
                    CstNumberLiteral(i),
                    CstNumberLiteral(i - targets.size),
                    false,
                    valueLoc
                )
            } else {
                CstNumberLiteral(
                    if (splatIndex in 0..<i) i - targets.size else i
                )
            }
            val call = CstCall(tempVar, "[]", indexer, location = valueLoc)
            assigns += transformMultiAssignTarget(target, call)
        }

        return CstExpressions(assigns, location = o.location)
    }

    private tailrec fun transformMultiAssignTarget(
        target: CstNode<*>,
        value: CstNode<*>
    ): CstNode<*> = when (target) {
        is CstSplat -> transformMultiAssignTarget(target.expression, value)
        is CstCall -> target.copy(
            name = "${target.name}=",
            args = target.args + value
        )
        else -> CstAssign(target, value, target.location)
    }
}

fun CstNode<*>.expandLiteral(project: Project) = project.resolveFacade.literalExpander.transform(this)
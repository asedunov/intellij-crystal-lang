package org.crystal.intellij.lang.ast

import com.intellij.openapi.project.Project
import com.intellij.util.SmartList
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.crystal.intellij.lang.ast.nodes.*
import org.crystal.intellij.lang.resolve.cache.resolveCache

class CstNormalizer(
    project: Project,
    private var currentDef: CstDef? = null
) : CstRecursiveTransformerBase() {
    private val resolveCache = project.resolveCache

    private var deadCode = false

    override fun beforeTransform(o: CstNode<*>) {
        deadCode = false
    }

    override fun afterTransform(o: CstNode<*>) {
        when (o) {
            is CstControlExpression -> deadCode = true
            is CstConditionalExpression, is CstExpressions, is CstBlock, is CstAssign -> {}
            else -> deadCode = false
        }
    }

    override fun transformAssign(o: CstAssign): CstNode<*> {
        val node = super.transformAssign(o)
        if (node !is CstAssign) return node
        return if (deadCode) node.value else node
    }

    override fun transformExpressions(o: CstExpressions): CstNode<*> {
        val exps = SmartList<CstNode<*>>()
        for (exp in o.expressions) {
            when (val newExp = exp.transform(this)) {
                is CstExpressions -> exps.addAll(newExp.expressions)
                else -> exps.add(newExp)
            }
            if (deadCode) break
        }
        if (exps.isEmpty()) return CstNop
        return o.copy(expressions = exps)
    }

    override fun transformIf(o: CstIf): CstNode<*> {
        val newCondition = o.condition.transform(this)

        val newThen = o.thenBranch.transform(this)
        val thenDeadCode = deadCode

        val newElse = o.elseBranch.transform(this)
        val elseDeadCode = deadCode

        deadCode = thenDeadCode && elseDeadCode
        return o.copy(
            condition = newCondition,
            thenBranch = newThen,
            elseBranch = newElse
        )
    }

    override fun transformMacro(o: CstMacro) = o

    override fun transformStringInterpolation(o: CstStringInterpolation): CstNode<*> {
        return o.expressions.singleOrNull() as? CstStringLiteral ?: super.transformStringInterpolation(o)
    }

    override fun transformUnless(o: CstUnless): CstNode<*> = CstIf(
        condition = o.condition,
        thenBranch = o.elseBranch,
        elseBranch = o.thenBranch,
        location = o.location
    ).transform(this)

    override fun transformUntil(o: CstUntil): CstNode<*> {
        val node = super.transformUntil(o)
        if (node !is CstUntil) return node
        val notExp = CstNot(node.condition, node.condition.location)
        return CstWhile(notExp, node.body, node.location)
    }

    override fun transformDef(o: CstDef): CstNode<*> {
        currentDef = o
        val node = super.transformDef(o)
        if (node !is CstDef) return node
        currentDef = null

        return node
    }

    override fun transformBlock(o: CstBlock): CstNode<*> {
        val node = super.transformBlock(o)
        if (node !is CstBlock) return node

        val unpacks = node.unpacks
        if (unpacks.isEmpty()) return node

        val extraExpressions = SmartList<CstNode<*>>()
        val nextUnpacks = ArrayDeque<Pair<String, CstExpressions>>()

        val unpackVars = Int2ObjectOpenHashMap<CstVar>(unpacks.size)
        unpacks.keys.intIterator().forEachRemaining { index ->
            val expressions = unpacks.get(index)
            val tempName = resolveCache.newTempVarName()
            val tempVar = CstVar(tempName, node.args[index].location)
            unpackVars.put(index, tempVar)
            extraExpressions += blockUnpackMultiAssign(tempName, expressions, nextUnpacks)
        }

        while (nextUnpacks.isNotEmpty()) {
            val (varName, expressions) = nextUnpacks.removeFirst()
            extraExpressions += blockUnpackMultiAssign(varName, expressions, nextUnpacks)
        }

        val body = node.body
        val curExpressions = when (body) {
            is CstNop -> emptyList()
            is CstExpressions -> body.expressions
            else -> listOf(body)
        }
        val newBody = CstExpressions(extraExpressions + curExpressions, location = body.location)

        return node.copy(
            args = node.args.mapIndexed { i, arg -> unpackVars.get(i) ?: arg },
            body = newBody
        )
    }

    private fun blockUnpackMultiAssign(
        varName: String,
        expressions: CstExpressions,
        nextUnpacks: ArrayDeque<Pair<String, CstExpressions>>
    ): CstMultiAssign {
        val targets = expressions.expressions.mapNotNull { exp ->
            when (exp) {
                is CstVar, is CstUnderscore, is CstSplat -> exp
                is CstExpressions -> {
                    val nextTempName = resolveCache.newTempVarName()
                    nextUnpacks.addLast(nextTempName to exp)
                    CstVar(nextTempName, exp.location)
                }
                else -> null
            }
        }
        val values = listOf(CstVar(varName, expressions.location))
        return CstMultiAssign(targets, values, expressions.location)
    }

    override fun transformCall(o: CstCall): CstNode<*> {
        if (o.isSuper || o.isPreviousDef) {
            val newNamedArgs = SmartList(o.namedArgs)
            val newArgs = SmartList(o.args)
            if (newArgs.isEmpty() && newNamedArgs.isEmpty() && !o.hasParentheses) {
                val currentDef = currentDef
                if (currentDef != null) {
                    val splatIndex = currentDef.splatIndex
                    currentDef.args.forEachIndexed { i, arg ->
                        when (splatIndex) {
                            in 0..<i -> {
                                newNamedArgs += CstNamedArgument(arg.externalName, CstVar(arg.name))
                            }
                            i -> {
                                if (arg.externalName.isNotEmpty()) {
                                    newArgs += CstSplat(CstVar(arg.name))
                                }
                            }
                            else -> {
                                newArgs += CstVar(arg.name)
                            }
                        }
                    }

                    val doubleSplat = currentDef.doubleSplat
                    if (doubleSplat != null) {
                        newArgs += CstDoubleSplat(CstVar(doubleSplat.name))
                    }

                    return o.copy(
                        args = newArgs,
                        namedArgs = newNamedArgs,
                        hasParentheses = true
                    )
                }
            }
        }

        val obj = o.obj
        if (o.name in COMPARISONS && obj is CstCall && obj.name in COMPARISONS) {
            val left: CstNode<*>
            val right: CstNode<*>
            when (val middle = obj.args.firstOrNull() ?: return super.transformCall(o)) {
                is CstNumberLiteral, is CstVar, is CstInstanceVar -> {
                    val newArgs = o.args.transform<CstNode<*>>()
                    left = obj
                    right = CstCall(middle, o.name, newArgs, location = middle.location)
                }
                else -> {
                    val tempVar = resolveCache.newTempVar()
                    val tempAssign = CstAssign(tempVar, middle, middle.location)
                    left = CstCall(obj.obj, obj.name, tempAssign, location = obj.obj?.location)
                    right = CstCall(tempVar, o.name, o.args, location = o.location)
                }
            }
            return CstAnd(left, right, left.location).transform(this)
        }

        return super.transformCall(o)
    }

    override fun transformOpAssign(o: CstOpAssign): CstNode<*> {
        val node = super.transformOpAssign(o)
        if (node !is CstOpAssign) return node

        val target = node.target
        return when {
            target !is CstCall -> transformOpAssignSimple(node, target)
            target.name == "[]" -> transformOpAssignIndex(node, target)
            else -> transformOpAssignCall(node, target)
        }
    }

    private fun transformOpAssignSimple(node: CstOpAssign, target: CstNode<*>): CstNode<*> {
        return when (node.op) {
            "&&" -> {
                val assign = CstAssign(target, node.value, node.location)
                CstAnd(target, assign, node.location)
            }

            "||" -> {
                val assign = CstAssign(target, node.value, node.location)
                CstOr(target, assign, node.location)
            }

            else -> {
                val call = CstCall(
                    obj = target,
                    name = node.op,
                    arg = node.value,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
                CstAssign(target, call, node.location)
            }
        }
    }

    private fun transformOpAssignCall(node: CstOpAssign, target: CstCall): CstNode<*> {
        val obj = target.obj ?: return node

        val tmp: CstNode<*>
        val assign: CstAssign?
        when (obj) {
            is CstVar, is CstInstanceVar, is CstClassVar, is CstSimpleLiteral -> {
                tmp = obj
                assign = null
            }
            else -> {
                tmp = resolveCache.newTempVar()
                assign = CstAssign(tmp, obj, node.location)
            }
        }

        var call: CstNode<*> = CstCall(
            obj = tmp,
            name = target.name,
            location = node.location,
            nameLocation = node.nameLocation
        )

        when (node.op) {
            "||" -> {
                val right = CstCall(
                    obj = tmp,
                    name = "${target.name}=",
                    arg = node.value,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
                call = CstOr(call, right, node.location)
            }

            "&&" -> {
                val right = CstCall(
                    obj = tmp,
                    name = "${target.name}=",
                    arg = node.value,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
                call = CstAnd(call, right, node.location)
            }

            else -> {
                call = CstCall(
                    obj = call,
                    name = node.op,
                    arg = node.value,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
                call = CstCall(
                    tmp,
                    "${target.name}=",
                    call,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
            }
        }

        return if (assign != null) CstExpressions(listOf(assign, call), location = node.location) else call
    }

    private fun transformOpAssignIndex(node: CstOpAssign, target: CstCall): CstNode<*> {
        val obj = target.obj ?: return node

        val tmpArgs = target.args.mapTo(ArrayList<CstNode<*>>()) { resolveCache.newTempVar() }
        var tmp: CstNode<*> = resolveCache.newTempVar()

        val tmpAssigns = ArrayList<CstNode<*>>(tmpArgs.size + 1)
        tmpArgs.forEachIndexed { i, variable ->
            val arg = target.args[i]
            if (arg is CstSimpleLiteral) {
                tmpArgs[i] = arg
            }
            else {
                tmpAssigns += CstAssign(variable, arg, node.location)
            }
        }

        when (obj) {
            is CstVar, is CstInstanceVar, is CstClassVar, is CstSimpleLiteral -> {
                tmp = obj
            }
            else -> {
                tmpAssigns += CstAssign(tmp, obj, node.location)
            }
        }

        var call: CstNode<*>
        when (node.op) {
            "||" -> {
                call = CstCall(
                    obj = tmp,
                    name = "[]?",
                    args = tmpArgs,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
                val args = tmpArgs + node.value
                val right = CstCall(
                    obj = tmp,
                    name = "[]=",
                    args = args,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
                call = CstOr(call, right, node.location)
            }

            "&&" -> {
                call = CstCall(
                    obj = tmp,
                    name = "[]?",
                    args = tmpArgs,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
                val args = tmpArgs + node.value
                val right = CstCall(
                    obj = tmp,
                    name = "[]=",
                    args = args,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
                call = CstAnd(call, right, node.location)
            }

            else -> {
                call = CstCall(
                    obj = tmp,
                    name = "[]",
                    args = tmpArgs,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
                call = CstCall(
                    obj = call,
                    name = node.op,
                    arg = node.value,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
                val args = tmpArgs + call
                call = CstCall(
                    obj = tmp,
                    name = "[]=",
                    args = args,
                    location = node.location,
                    nameLocation = node.nameLocation
                )
            }
        }

        return if (tmpAssigns.isNotEmpty()) {
            CstExpressions(tmpAssigns + call, location = node.location)
        } else {
            call
        }
    }
}

private val COMPARISONS = setOf("<=", "<", "!=", "==", "===", ">", ">=")

fun CstNode<*>.normalize(
    project: Project,
    currentDef: CstDef? = null
) = transform(CstNormalizer(project, currentDef))
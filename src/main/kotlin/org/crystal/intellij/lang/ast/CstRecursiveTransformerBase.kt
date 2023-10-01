package org.crystal.intellij.lang.ast

import com.intellij.util.SmartList
import org.crystal.intellij.lang.ast.nodes.*

open class CstRecursiveTransformerBase : CstTransformer() {
    override fun transformAlias(o: CstAlias): CstNode<*> = o.copy(
        value = o.value.transform(this)
    )

    override fun transformAnd(o: CstAnd): CstNode<*> = o.copy(
        left = o.left.transform(this),
        right = o.right.transform(this)
    )

    override fun transformArg(o: CstArg): CstNode<*> {
        val defaultValue = o.defaultValue
        val restriction = o.restriction
        if (defaultValue == null && restriction == null) return o
        return o.copy(
            defaultValue = o.defaultValue?.transform(this),
            restriction = o.restriction?.transform(this)
        )
    }

    override fun transformArrayLiteral(o: CstArrayLiteral): CstNode<*> = o.copy(
        elements = o.elements.transform(),
        elementType = o.elementType?.transform(this)
    )

    override fun transformAsm(o: CstAsm): CstNode<*> = o.copy(
        outputs = o.outputs.transform(),
        inputs = o.inputs.transform()
    )

    override fun transformAsmOperand(o: CstAsmOperand): CstNode<*> = o.copy(exp = o.exp.transform(this))

    override fun transformAssign(o: CstAssign): CstNode<*> = o.copy(
        target = o.target.transform(this),
        value = o.value.transform(this)
    )

    override fun transformBlock(o: CstBlock): CstNode<*> = o.copy(
        args = o.args.transform(),
        body = o.body.transform(this)
    )

    override fun transformBreak(o: CstBreak): CstNode<*> {
        val expression = o.expression ?: return o
        return o.copy(expression = expression.transform(this))
    }

    override fun transformCall(o: CstCall): CstNode<*> = o.copy(
        obj = o.obj?.transform(this),
        args = o.args.transform(),
        namedArgs = o.namedArgs.transform(),
        blockArg = o.blockArg?.transform(this),
        block = o.block?.transformAs<CstBlock>(this)
    )

    override fun transformCase(o: CstCase): CstNode<*> = o.copy(
        condition = o.condition?.transform(this),
        whenBranches = o.whenBranches.transform(),
        elseBranch = o.elseBranch?.transform(this)
    )

    override fun transformCast(o: CstCast): CstNode<*> = o.copy(
        obj = o.obj.transform(this),
        type = o.type.transform(this)
    )

    override fun transformClassDef(o: CstClassDef): CstNode<*> = o.copy(
        body = o.body.transform(this),
        superclass = o.superclass?.transform(this)
    )

    override fun transformCStructOrUnionDef(o: CstCStructOrUnionDef): CstNode<*> = o.copy(
        body = o.body.transform(this)
    )

    override fun transformDef(o: CstDef): CstNode<*> = o.copy(
        args = o.args.transform(),
        body = o.body.transform(this),
        receiver = o.receiver?.transform(this),
        doubleSplat = o.doubleSplat?.transformAs<CstArg>(this),
        blockArg = o.blockArg?.transformAs<CstArg>(this)
    )

    override fun transformDoubleSplat(o: CstDoubleSplat): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    override fun transformEnumDef(o: CstEnumDef): CstNode<*> = o.copy(
        members = o.members.transform()
    )

    override fun transformExceptionHandler(o: CstExceptionHandler): CstNode<*> = o.copy(
        body = o.body.transform(this),
        rescues = o.rescues.transform(),
        elseBranch = o.elseBranch?.transform(this),
        ensure = o.ensure?.transform(this),
    )

    override fun transformExpressions(o: CstExpressions): CstNode<*> {
        val expressions = SmartList<CstNode<*>>()
        for (exp in o.expressions) {
            when (val newExp = exp.transform(this)) {
                is CstExpressions -> expressions.addAll(newExp.expressions)
                else -> expressions.add(newExp)
            }
        }
        return expressions.singleOrNull() ?: o.copy(expressions = expressions)
    }

    override fun transformExtend(o: CstExtend): CstNode<*> = o.copy(
        name = o.name.transform(this)
    )

    override fun transformFunDef(o: CstFunDef): CstNode<*> {
        val body = o.body ?: return o
        return o.copy(body = body)
    }

    override fun transformGeneric(o: CstGeneric): CstNode<*> = o.copy(
        name = o.name.transform(this),
        typeVars = o.typeVars.transform()
    )

    override fun transformHashLiteral(o: CstHashLiteral): CstNode<*> = o.copy(
        entries = o.entries.map { it.transform() },
        elementType = o.elementType?.transform()
    )

    private fun CstHashLiteral.Entry.transform() = CstHashLiteral.Entry(
        key.transform(this@CstRecursiveTransformerBase),
        value.transform(this@CstRecursiveTransformerBase)
    )

    override fun transformIf(o: CstIf): CstNode<*> = o.copy(
        condition = o.condition.transform(this),
        thenBranch = o.thenBranch.transform(this),
        elseBranch = o.elseBranch.transform(this)
    )

    override fun transformInclude(o: CstInclude): CstNode<*> = o.copy(
        name = o.name.transform(this)
    )

    override fun transformInstanceSizeOf(o: CstInstanceSizeOf): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    override fun transformIsA(o: CstIsA): CstNode<*> = o.copy(
        obj = o.obj.transform(this),
        arg = o.arg.transform(this)
    )

    override fun transformLibDef(o: CstLibDef): CstNode<*> = o.copy(
        body = o.body.transform(this)
    )

    override fun transformMacro(o: CstMacro): CstNode<*> = o.copy(
        args = o.args.transform(),
        doubleSplat = o.doubleSplat?.transformAs<CstArg>(this),
        blockArg = o.blockArg?.transformAs<CstArg>(this),
        body = o.body.transform(this)
    )

    override fun transformMacroExpression(o: CstMacroExpression): CstNode<*> = o.copy(
        exp = o.exp.transform(this)
    )

    override fun transformMacroFor(o: CstMacroFor): CstNode<*> = o.copy(
        exp = o.exp.transform(this),
        body = o.body.transform(this)
    )

    override fun transformMacroIf(o: CstMacroIf): CstNode<*> = o.copy(
        condition = o.condition.transform(this),
        thenBranch = o.thenBranch.transform(this),
        elseBranch = o.elseBranch.transform(this)
    )

    override fun transformMetaclass(o: CstMetaclass): CstNode<*> = o.copy(
        name = o.name.transform(this)
    )

    override fun transformModuleDef(o: CstModuleDef): CstNode<*> = o.copy(
        body = o.body.transform(this)
    )

    override fun transformMultiAssign(o: CstMultiAssign): CstNode<*> = o.copy(
        targets = o.targets.transform(),
        values = o.values.transform()
    )

    override fun transformNamedArgument(o: CstNamedArgument): CstNode<*> = o.copy(
        value = o.value.transform(this)
    )

    override fun transformNamedTupleLiteral(o: CstNamedTupleLiteral): CstNode<*> = o.copy(
        entries = o.entries.map { it.transform() }
    )

    private fun CstNamedTupleLiteral.Entry.transform() = CstNamedTupleLiteral.Entry(
        key,
        value.transform(this@CstRecursiveTransformerBase)
    )

    override fun transformNext(o: CstNext): CstNode<*> {
        val expression = o.expression ?: return o
        return o.copy(expression = expression.transform(this))
    }

    override fun transformNilableCast(o: CstNilableCast): CstNode<*> = o.copy(
        obj = o.obj.transform(this),
        type = o.type.transform(this)
    )

    override fun transformNot(o: CstNot): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    override fun transformOffsetOf(o: CstOffsetOf): CstNode<*> = o.copy(
        type = o.type.transform(this),
        offset = o.offset.transform(this)
    )

    override fun transformOpAssign(o: CstOpAssign): CstNode<*> = o.copy(
        target = o.target.transform(this),
        value = o.value.transform(this)
    )

    override fun transformOr(o: CstOr): CstNode<*> = o.copy(
        left = o.left.transform(this),
        right = o.right.transform(this)
    )

    override fun transformOut(o: CstOut): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    override fun transformPointerOf(o: CstPointerOf): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    override fun transformProcLiteral(o: CstProcLiteral): CstNode<*> {
        val def = o.def
        return o.copy(
            def = def.copy(
                body = def.body.transform(this)
            )
        )
    }

    override fun transformProcNotation(o: CstProcNotation): CstNode<*> = o.copy(
        inputs = o.inputs.transform(),
        output = o.output?.transform(this)
    )

    override fun transformProcPointer(o: CstProcPointer): CstNode<*> {
        val obj = o.obj ?: return o
        return o.copy(obj = obj)
    }

    override fun transformRangeLiteral(o: CstRangeLiteral): CstNode<*> = o.copy(
        from = o.from.transform(this),
        to = o.to.transform(this),
    )

    override fun transformReadInstanceVar(o: CstReadInstanceVar): CstNode<*> = o.copy(
        receiver = o.receiver.transform(this)
    )

    override fun transformRegexLiteral(o: CstRegexLiteral): CstNode<*> = o.copy(
        source = o.source.transform(this)
    )

    override fun transformRescue(o: CstRescue): CstNode<*> = o.copy(
        body = o.body.transform(this),
        types = o.types.transform()
    )

    override fun transformRespondsTo(o: CstRespondsTo): CstNode<*> = o.copy(
        obj = o.obj.transform(this)
    )

    override fun transformReturn(o: CstReturn): CstNode<*> {
        val expression = o.expression ?: return o
        return o.copy(expression = expression.transform(this))
    }

    override fun transformSelect(o: CstSelect): CstNode<*> = o.copy(
        whens = o.whens.map {
            CstSelect.When(it.condition.transform(this), it.body.transform(this))
        },
        elseBranch = o.elseBranch?.transform(this)
    )

    override fun transformSizeOf(o: CstSizeOf): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    override fun transformSplat(o: CstSplat): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    override fun transformStringInterpolation(o: CstStringInterpolation): CstNode<*> = o.copy(
        expressions = o.expressions.transform()
    )

    override fun transformTupleLiteral(o: CstTupleLiteral): CstNode<*> = o.copy(
        elements = o.elements.transform()
    )

    override fun transformTypeDeclaration(o: CstTypeDeclaration): CstNode<*> = o.copy(
        variable = o.variable.transform(this),
        type = o.type.transform(this),
        value = o.value?.transform(this)
    )

    override fun transformTypeOf(o: CstTypeOf): CstNode<*> = o.copy(
        expressions = o.expressions.transform()
    )

    override fun transformUninitializedVar(o: CstUninitializedVar): CstNode<*> = o.copy(
        variable = o.variable.transform(this),
        declaredType = o.declaredType.transform(this),
    )

    override fun transformUnion(o: CstUnion): CstNode<*> = o.copy(
        types = o.types.transform()
    )

    override fun transformUnless(o: CstUnless): CstNode<*> = o.copy(
        condition = o.condition.transform(this),
        thenBranch = o.thenBranch.transform(this),
        elseBranch = o.elseBranch.transform(this)
    )

    override fun transformUntil(o: CstUntil): CstNode<*> = o.copy(
        condition = o.condition.transform(this),
        body = o.body.transform(this)
    )

    override fun transformVisibilityModifier(o: CstVisibilityModifier): CstNode<*> = o.copy(
       exp  = o.exp.transform(this)
    )

    override fun transformWhen(o: CstWhen): CstNode<*> = o.copy(
        conditions = o.conditions.transform(),
        body = o.body.transform(this)
    )

    override fun transformWhile(o: CstWhile): CstNode<*> = o.copy(
        condition = o.condition.transform(this),
        body = o.body.transform(this)
    )

    override fun transformYield(o: CstYield): CstNode<*> = o.copy(
        expressions = o.expressions.transform(),
        scope = o.scope?.transform(this)
    )
}
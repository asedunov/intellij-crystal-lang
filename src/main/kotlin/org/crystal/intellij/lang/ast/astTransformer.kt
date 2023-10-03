package org.crystal.intellij.lang.ast

import com.intellij.util.SmartList
import org.crystal.intellij.lang.ast.nodes.*

open class CstTransformer {
    protected open fun beforeTransform(o: CstNode<*>) {}

    protected open fun afterTransform(o: CstNode<*>) {}

    open fun transformAlias(o: CstAlias): CstNode<*> = o.copy(
        value = o.value.transform(this)
    )

    open fun transformAnd(o: CstAnd): CstNode<*> = o.copy(
        left = o.left.transform(this),
        right = o.right.transform(this)
    )

    open fun transformAnnotation(o: CstAnnotation): CstNode<*> = o

    open fun transformAnnotationDef(o: CstAnnotationDef): CstNode<*> = o

    open fun transformArg(o: CstArg): CstNode<*> {
        val defaultValue = o.defaultValue
        val restriction = o.restriction
        if (defaultValue == null && restriction == null) return o
        return o.copy(
            defaultValue = o.defaultValue?.transform(this),
            restriction = o.restriction?.transform(this)
        )
    }

    open fun transformArrayLiteral(o: CstArrayLiteral): CstNode<*> = o.copy(
        elements = o.elements.transform(),
        elementType = o.elementType?.transform(this)
    )

    open fun transformAsm(o: CstAsm): CstNode<*> = o.copy(
        outputs = o.outputs.transform(),
        inputs = o.inputs.transform()
    )

    open fun transformAsmOperand(o: CstAsmOperand): CstNode<*> = o.copy(exp = o.exp.transform(this))

    open fun transformAssign(o: CstAssign): CstNode<*> = o.copy(
        target = o.target.transform(this),
        value = o.value.transform(this)
    )

    open fun transformBlock(o: CstBlock): CstNode<*> = o.copy(
        args = o.args.transform(),
        body = o.body.transform(this)
    )

    open fun transformBoolLiteral(o: CstBoolLiteral): CstNode<*> = o

    open fun transformBreak(o: CstBreak): CstNode<*> {
        val expression = o.expression ?: return o
        return o.copy(expression = expression.transform(this))
    }

    open fun transformCall(o: CstCall): CstNode<*> = o.copy(
        obj = o.obj?.transform(this),
        args = o.args.transform(),
        namedArgs = o.namedArgs.transform(),
        blockArg = o.blockArg?.transform(this),
        block = o.block?.transformAs<CstBlock>(this)
    )

    open fun transformCase(o: CstCase): CstNode<*> = o.copy(
        condition = o.condition?.transform(this),
        whenBranches = o.whenBranches.transform(),
        elseBranch = o.elseBranch?.transform(this)
    )

    open fun transformCast(o: CstCast): CstNode<*> = o.copy(
        obj = o.obj.transform(this),
        type = o.type.transform(this)
    )

    open fun transformCharLiteral(o: CstCharLiteral): CstNode<*> = o

    open fun transformClassDef(o: CstClassDef): CstNode<*> = o.copy(
        body = o.body.transform(this),
        superclass = o.superclass?.transform(this)
    )

    open fun transformClassVar(o: CstClassVar): CstNode<*> = o

    open fun transformCStructOrUnionDef(o: CstCStructOrUnionDef): CstNode<*> = o.copy(
        body = o.body.transform(this)
    )

    open fun transformDef(o: CstDef): CstNode<*> = o.copy(
        args = o.args.transform(),
        body = o.body.transform(this),
        receiver = o.receiver?.transform(this),
        doubleSplat = o.doubleSplat?.transformAs<CstArg>(this),
        blockArg = o.blockArg?.transformAs<CstArg>(this)
    )

    open fun transformDoubleSplat(o: CstDoubleSplat): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    open fun transformEnumDef(o: CstEnumDef): CstNode<*> = o.copy(
        members = o.members.transform()
    )

    open fun transformExceptionHandler(o: CstExceptionHandler): CstNode<*> = o.copy(
        body = o.body.transform(this),
        rescues = o.rescues.transform(),
        elseBranch = o.elseBranch?.transform(this),
        ensure = o.ensure?.transform(this),
    )

    open fun transformExpressions(o: CstExpressions): CstNode<*> {
        val expressions = SmartList<CstNode<*>>()
        for (exp in o.expressions) {
            when (val newExp = exp.transform(this)) {
                is CstExpressions -> expressions.addAll(newExp.expressions)
                else -> expressions.add(newExp)
            }
        }
        return expressions.singleOrNull() ?: o.copy(expressions = expressions)
    }

    open fun transformExtend(o: CstExtend): CstNode<*> = o.copy(
        name = o.name.transform(this)
    )

    open fun transformExternalVar(o: CstExternalVar): CstNode<*> = o

    open fun transformFunDef(o: CstFunDef): CstNode<*> {
        val body = o.body ?: return o
        return o.copy(body = body)
    }

    open fun transformGeneric(o: CstGeneric): CstNode<*> = o.copy(
        name = o.name.transform(this),
        typeVars = o.typeVars.transform()
    )

    open fun transformGlobal(o: CstGlobal): CstNode<*> = o

    open fun transformHashLiteral(o: CstHashLiteral): CstNode<*> = o.copy(
        entries = o.entries.map { it.transform() },
        elementType = o.elementType?.transform()
    )

    private fun CstHashLiteral.Entry.transform() = CstHashLiteral.Entry(
        key.transform(this@CstTransformer),
        value.transform(this@CstTransformer)
    )

    open fun transformIf(o: CstIf): CstNode<*> = o.copy(
        condition = o.condition.transform(this),
        thenBranch = o.thenBranch.transform(this),
        elseBranch = o.elseBranch.transform(this)
    )

    open fun transformImplicitObj(o: CstImplicitObj): CstNode<*> = o

    open fun transformInclude(o: CstInclude): CstNode<*> = o.copy(
        name = o.name.transform(this)
    )

    open fun transformInstanceSizeOf(o: CstInstanceSizeOf): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    open fun transformInstanceVar(o: CstInstanceVar): CstNode<*> = o

    open fun transformIsA(o: CstIsA): CstNode<*> = o.copy(
        receiver = o.receiver.transform(this),
        arg = o.arg.transform(this)
    )

    open fun transformLibDef(o: CstLibDef): CstNode<*> = o.copy(
        body = o.body.transform(this)
    )

    open fun transformMacro(o: CstMacro): CstNode<*> = o.copy(
        args = o.args.transform(),
        doubleSplat = o.doubleSplat?.transformAs<CstArg>(this),
        blockArg = o.blockArg?.transformAs<CstArg>(this),
        body = o.body.transform(this)
    )

    open fun transformMacroExpression(o: CstMacroExpression): CstNode<*> = o.copy(
        exp = o.exp.transform(this)
    )

    open fun transformMacroFor(o: CstMacroFor): CstNode<*> = o.copy(
        exp = o.exp.transform(this),
        body = o.body.transform(this)
    )

    open fun transformMacroIf(o: CstMacroIf): CstNode<*> = o.copy(
        condition = o.condition.transform(this),
        thenBranch = o.thenBranch.transform(this),
        elseBranch = o.elseBranch.transform(this)
    )

    open fun transformMacroLiteral(o: CstMacroLiteral): CstNode<*> = o

    open fun transformMacroVar(o: CstMacroVar): CstNode<*> = o

    open fun transformMacroVerbatim(o: CstMacroVerbatim): CstNode<*> = o

    open fun transformMagicConstant(o: CstMagicConstant): CstNode<*> = o

    open fun transformMetaclass(o: CstMetaclass): CstNode<*> = o.copy(
        name = o.name.transform(this)
    )

    open fun transformModuleDef(o: CstModuleDef): CstNode<*> = o.copy(
        body = o.body.transform(this)
    )

    open fun transformMultiAssign(o: CstMultiAssign): CstNode<*> = o.copy(
        targets = o.targets.transform(),
        values = o.values.transform()
    )

    open fun transformNamedArgument(o: CstNamedArgument): CstNode<*> = o.copy(
        value = o.value.transform(this)
    )

    open fun transformNamedTupleLiteral(o: CstNamedTupleLiteral): CstNode<*> = o.copy(
        entries = o.entries.map { it.transform() }
    )

    private fun CstNamedTupleLiteral.Entry.transform() = CstNamedTupleLiteral.Entry(
        key,
        value.transform(this@CstTransformer)
    )

    open fun transformNext(o: CstNext): CstNode<*> {
        val expression = o.expression ?: return o
        return o.copy(expression = expression.transform(this))
    }

    open fun transformNilableCast(o: CstNilableCast): CstNode<*> = o.copy(
        obj = o.obj.transform(this),
        type = o.type.transform(this)
    )

    open fun transformNilLiteral(o: CstNilLiteral): CstNode<*> = o

    open fun transformNop(o: CstNop): CstNode<*> = o

    open fun transformNot(o: CstNot): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    open fun transformNumberLiteral(o: CstNumberLiteral): CstNode<*> = o

    open fun transformOffsetOf(o: CstOffsetOf): CstNode<*> = o.copy(
        type = o.type.transform(this),
        offset = o.offset.transform(this)
    )

    open fun transformOpAssign(o: CstOpAssign): CstNode<*> = o.copy(
        target = o.target.transform(this),
        value = o.value.transform(this)
    )

    open fun transformOr(o: CstOr): CstNode<*> = o.copy(
        left = o.left.transform(this),
        right = o.right.transform(this)
    )

    open fun transformOut(o: CstOut): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    open fun transformPath(o: CstPath): CstNode<*> = o

    open fun transformPointerOf(o: CstPointerOf): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    open fun transformProcLiteral(o: CstProcLiteral): CstNode<*> {
        val def = o.def
        return o.copy(
            def = def.copy(
                body = def.body.transform(this)
            )
        )
    }

    open fun transformProcNotation(o: CstProcNotation): CstNode<*> = o.copy(
        inputs = o.inputs.transform(),
        output = o.output?.transform(this)
    )

    open fun transformProcPointer(o: CstProcPointer): CstNode<*> {
        val obj = o.obj ?: return o
        return o.copy(obj = obj)
    }

    open fun transformRangeLiteral(o: CstRangeLiteral): CstNode<*> = o.copy(
        from = o.from.transform(this),
        to = o.to.transform(this),
    )

    open fun transformReadInstanceVar(o: CstReadInstanceVar): CstNode<*> = o.copy(
        receiver = o.receiver.transform(this)
    )

    open fun transformRegexLiteral(o: CstRegexLiteral): CstNode<*> = o.copy(
        source = o.source.transform(this)
    )

    open fun transformRequire(o: CstRequire): CstNode<*> = o

    open fun transformRescue(o: CstRescue): CstNode<*> = o.copy(
        body = o.body.transform(this),
        types = o.types.transform()
    )

    open fun transformRespondsTo(o: CstRespondsTo): CstNode<*> = o.copy(
        receiver = o.receiver.transform(this)
    )

    open fun transformReturn(o: CstReturn): CstNode<*> {
        val expression = o.expression ?: return o
        return o.copy(expression = expression.transform(this))
    }

    open fun transformSelect(o: CstSelect): CstNode<*> = o.copy(
        whens = o.whens.map {
            CstSelect.When(it.condition.transform(this), it.body.transform(this))
        },
        elseBranch = o.elseBranch?.transform(this)
    )

    open fun transformSelf(o: CstSelf): CstNode<*> = o

    open fun transformSizeOf(o: CstSizeOf): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    open fun transformSplat(o: CstSplat): CstNode<*> = o.copy(
        expression = o.expression.transform(this)
    )

    open fun transformStringInterpolation(o: CstStringInterpolation): CstNode<*> = o.copy(
        expressions = o.expressions.transform()
    )

    open fun transformStringLiteral(o: CstStringLiteral): CstNode<*> = o

    open fun transformSymbolLiteral(o: CstSymbolLiteral): CstNode<*> = o

    open fun transformTupleLiteral(o: CstTupleLiteral): CstNode<*> = o.copy(
        elements = o.elements.transform()
    )

    open fun transformTypeDeclaration(o: CstTypeDeclaration): CstNode<*> = o.copy(
        variable = o.variable.transform(this),
        type = o.type.transform(this),
        value = o.value?.transform(this)
    )

    open fun transformTypeDef(o: CstTypeDef): CstNode<*> = o

    open fun transformTypeOf(o: CstTypeOf): CstNode<*> = o.copy(
        expressions = o.expressions.transform()
    )

    open fun transformUnderscore(o: CstUnderscore): CstNode<*> = o

    open fun transformUninitializedVar(o: CstUninitializedVar): CstNode<*> = o.copy(
        variable = o.variable.transform(this),
        declaredType = o.declaredType.transform(this),
    )

    open fun transformUnion(o: CstUnion): CstNode<*> = o.copy(
        types = o.types.transform()
    )

    open fun transformUnless(o: CstUnless): CstNode<*> = o.copy(
        condition = o.condition.transform(this),
        thenBranch = o.thenBranch.transform(this),
        elseBranch = o.elseBranch.transform(this)
    )

    open fun transformUntil(o: CstUntil): CstNode<*> = o.copy(
        condition = o.condition.transform(this),
        body = o.body.transform(this)
    )

    open fun transformVar(o: CstVar): CstNode<*> = o

    open fun transformVisibilityModifier(o: CstVisibilityModifier): CstNode<*> = o.copy(
       exp  = o.exp.transform(this)
    )

    open fun transformWhen(o: CstWhen): CstNode<*> = o.copy(
        conditions = o.conditions.transform(),
        body = o.body.transform(this)
    )

    open fun transformWhile(o: CstWhile): CstNode<*> = o.copy(
        condition = o.condition.transform(this),
        body = o.body.transform(this)
    )

    open fun transformYield(o: CstYield): CstNode<*> = o.copy(
        expressions = o.expressions.transform(),
        scope = o.scope?.transform(this)
    )

    protected inline fun <reified T : CstNode<*>> List<CstNode<*>>.transform() = mapNotNull {
        transform(it) as? T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : CstNode<*>> transform(o: T): T {
        beforeTransform(o)
        val result = o.acceptTransformer(this) as T
        afterTransform(o)
        return result
    }
}

inline fun <reified T : CstNode<*>> CstNode<*>.transformAs(transformer: CstTransformer): T? {
    return transformer.transform(this) as? T
}

fun CstNode<*>.transform(transformer: CstTransformer) = transformer.transform(this)
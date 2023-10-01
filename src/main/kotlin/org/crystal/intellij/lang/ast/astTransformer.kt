package org.crystal.intellij.lang.ast

import org.crystal.intellij.lang.ast.nodes.*

open class CstTransformer {
    protected open fun beforeTransform(o: CstNode<*>) {}

    protected open fun afterTransform(o: CstNode<*>) {}

    open fun transformAlias(o: CstAlias): CstNode<*> = o

    open fun transformAnd(o: CstAnd): CstNode<*> = o

    open fun transformAnnotation(o: CstAnnotation): CstNode<*> = o

    open fun transformAnnotationDef(o: CstAnnotationDef): CstNode<*> = o

    open fun transformArg(o: CstArg): CstNode<*> = o

    open fun transformArrayLiteral(o: CstArrayLiteral): CstNode<*> = o

    open fun transformAsm(o: CstAsm): CstNode<*> = o

    open fun transformAsmOperand(o: CstAsmOperand): CstNode<*> = o

    open fun transformAssign(o: CstAssign): CstNode<*> = o

    open fun transformBlock(o: CstBlock): CstNode<*> = o

    open fun transformBoolLiteral(o: CstBoolLiteral): CstNode<*> = o

    open fun transformBreak(o: CstBreak): CstNode<*> = o

    open fun transformCall(o: CstCall): CstNode<*> = o

    open fun transformCase(o: CstCase): CstNode<*> = o

    open fun transformCast(o: CstCast): CstNode<*> = o

    open fun transformCharLiteral(o: CstCharLiteral): CstNode<*> = o

    open fun transformClassDef(o: CstClassDef): CstNode<*> = o

    open fun transformClassVar(o: CstClassVar): CstNode<*> = o

    open fun transformCStructOrUnionDef(o: CstCStructOrUnionDef): CstNode<*> = o

    open fun transformDef(o: CstDef): CstNode<*> = o

    open fun transformDoubleSplat(o: CstDoubleSplat): CstNode<*> = o

    open fun transformEnumDef(o: CstEnumDef): CstNode<*> = o

    open fun transformExceptionHandler(o: CstExceptionHandler): CstNode<*> = o

    open fun transformExpressions(o: CstExpressions): CstNode<*> = o

    open fun transformExtend(o: CstExtend): CstNode<*> = o

    open fun transformExternalVar(o: CstExternalVar): CstNode<*> = o

    open fun transformFunDef(o: CstFunDef): CstNode<*> = o

    open fun transformGeneric(o: CstGeneric): CstNode<*> = o

    open fun transformGlobal(o: CstGlobal): CstNode<*> = o

    open fun transformHashLiteral(o: CstHashLiteral): CstNode<*> = o

    open fun transformIf(o: CstIf): CstNode<*> = o

    open fun transformImplicitObj(o: CstImplicitObj): CstNode<*> = o

    open fun transformInclude(o: CstInclude): CstNode<*> = o

    open fun transformInstanceSizeOf(o: CstInstanceSizeOf): CstNode<*> = o

    open fun transformInstanceVar(o: CstInstanceVar): CstNode<*> = o

    open fun transformIsA(o: CstIsA): CstNode<*> = o

    open fun transformLibDef(o: CstLibDef): CstNode<*> = o

    open fun transformMacro(o: CstMacro): CstNode<*> = o

    open fun transformMacroExpression(o: CstMacroExpression): CstNode<*> = o

    open fun transformMacroFor(o: CstMacroFor): CstNode<*> = o

    open fun transformMacroIf(o: CstMacroIf): CstNode<*> = o

    open fun transformMacroLiteral(o: CstMacroLiteral): CstNode<*> = o

    open fun transformMacroVar(o: CstMacroVar): CstNode<*> = o

    open fun transformMacroVerbatim(o: CstMacroVerbatim): CstNode<*> = o

    open fun transformMagicConstant(o: CstMagicConstant): CstNode<*> = o

    open fun transformMetaclass(o: CstMetaclass): CstNode<*> = o

    open fun transformModuleDef(o: CstModuleDef): CstNode<*> = o

    open fun transformMultiAssign(o: CstMultiAssign): CstNode<*> = o

    open fun transformNamedArgument(o: CstNamedArgument): CstNode<*> = o

    open fun transformNamedTupleLiteral(o: CstNamedTupleLiteral): CstNode<*> = o

    open fun transformNext(o: CstNext): CstNode<*> = o

    open fun transformNilableCast(o: CstNilableCast): CstNode<*> = o

    open fun transformNilLiteral(o: CstNilLiteral): CstNode<*> = o

    open fun transformNop(o: CstNop): CstNode<*> = o

    open fun transformNot(o: CstNot): CstNode<*> = o

    open fun transformNumberLiteral(o: CstNumberLiteral): CstNode<*> = o

    open fun transformOffsetOf(o: CstOffsetOf): CstNode<*> = o

    open fun transformOpAssign(o: CstOpAssign): CstNode<*> = o

    open fun transformOr(o: CstOr): CstNode<*> = o

    open fun transformOut(o: CstOut): CstNode<*> = o

    open fun transformPath(o: CstPath): CstNode<*> = o

    open fun transformPointerOf(o: CstPointerOf): CstNode<*> = o

    open fun transformProcLiteral(o: CstProcLiteral): CstNode<*> = o

    open fun transformProcNotation(o: CstProcNotation): CstNode<*> = o

    open fun transformProcPointer(o: CstProcPointer): CstNode<*> = o

    open fun transformRangeLiteral(o: CstRangeLiteral): CstNode<*> = o

    open fun transformReadInstanceVar(o: CstReadInstanceVar): CstNode<*> = o

    open fun transformRegexLiteral(o: CstRegexLiteral): CstNode<*> = o

    open fun transformRequire(o: CstRequire): CstNode<*> = o

    open fun transformRescue(o: CstRescue): CstNode<*> = o

    open fun transformRespondsTo(o: CstRespondsTo): CstNode<*> = o

    open fun transformReturn(o: CstReturn): CstNode<*> = o

    open fun transformSelect(o: CstSelect): CstNode<*> = o

    open fun transformSelf(o: CstSelf): CstNode<*> = o

    open fun transformSizeOf(o: CstSizeOf): CstNode<*> = o

    open fun transformSplat(o: CstSplat): CstNode<*> = o

    open fun transformStringInterpolation(o: CstStringInterpolation): CstNode<*> = o

    open fun transformStringLiteral(o: CstStringLiteral): CstNode<*> = o

    open fun transformSymbolLiteral(o: CstSymbolLiteral): CstNode<*> = o

    open fun transformTupleLiteral(o: CstTupleLiteral): CstNode<*> = o

    open fun transformTypeDeclaration(o: CstTypeDeclaration): CstNode<*> = o

    open fun transformTypeDef(o: CstTypeDef): CstNode<*> = o

    open fun transformTypeOf(o: CstTypeOf): CstNode<*> = o

    open fun transformUnderscore(o: CstUnderscore): CstNode<*> = o

    open fun transformUninitializedVar(o: CstUninitializedVar): CstNode<*> = o

    open fun transformUnion(o: CstUnion): CstNode<*> = o

    open fun transformUnless(o: CstUnless): CstNode<*> = o

    open fun transformUnreachable(o: CstUnreachable): CstNode<*> = o

    open fun transformUntil(o: CstUntil): CstNode<*> = o

    open fun transformVar(o: CstVar): CstNode<*> = o

    open fun transformVisibilityModifier(o: CstVisibilityModifier): CstNode<*> = o

    open fun transformWhen(o: CstWhen): CstNode<*> = o

    open fun transformWhile(o: CstWhile): CstNode<*> = o

    open fun transformYield(o: CstYield): CstNode<*> = o

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
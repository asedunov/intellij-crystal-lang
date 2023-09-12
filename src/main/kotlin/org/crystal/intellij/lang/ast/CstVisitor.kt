package org.crystal.intellij.lang.ast

import org.crystal.intellij.lang.ast.nodes.*

open class CstVisitor {
    open fun visitAlias(o: CstAlias) = true

    open fun visitAnd(o: CstAnd) = true

    open fun visitAnnotation(o: CstAnnotation) = true

    open fun visitAnnotationDef(o: CstAnnotationDef) = true

    open fun visitArg(o: CstArg) = true

    open fun visitArrayLiteral(o: CstArrayLiteral) = true

    open fun visitAsm(o: CstAsm) = true

    open fun visitAsmOperand(o: CstAsmOperand) = true

    open fun visitAssign(o: CstAssign) = true

    open fun visitBlock(o: CstBlock) = true

    open fun visitBoolLiteral(o: CstBoolLiteral) = true

    open fun visitBreak(o: CstBreak) = true

    open fun visitCall(o: CstCall) = true

    open fun visitCase(o: CstCase) = true

    open fun visitCast(o: CstCast) = true

    open fun visitCharLiteral(o: CstCharLiteral) = true

    open fun visitClassDef(o: CstClassDef) = true

    open fun visitClassVar(o: CstClassVar) = true

    open fun visitCStructOrUnionDef(o: CstCStructOrUnionDef) = true

    open fun visitDef(o: CstDef) = true

    open fun visitDoubleSplat(o: CstDoubleSplat) = true

    open fun visitEnumDef(o: CstEnumDef) = true

    open fun visitExceptionHandler(o: CstExceptionHandler) = true

    open fun visitExpressions(o: CstExpressions) = true

    open fun visitExtend(o: CstExtend) = true

    open fun visitExternalVar(o: CstExternalVar) = true

    open fun visitFunDef(o: CstFunDef) = true

    open fun visitGeneric(o: CstGeneric) = true

    open fun visitGlobal(o: CstGlobal) = true

    open fun visitHashLiteral(o: CstHashLiteral) = true

    open fun visitIf(o: CstIf) = true

    open fun visitImplicitObj(o: CstImplicitObj) = true

    open fun visitInclude(o: CstInclude) = true

    open fun visitInstanceSizeOf(o: CstInstanceSizeOf) = true

    open fun visitInstanceVar(o: CstInstanceVar) = true

    open fun visitIsA(o: CstIsA) = true

    open fun visitLibDef(o: CstLibDef) = true

    open fun visitMacro(o: CstMacro) = true

    open fun visitMacroExpression(o: CstMacroExpression) = true

    open fun visitMacroFor(o: CstMacroFor) = true

    open fun visitMacroIf(o: CstMacroIf) = true

    open fun visitMacroLiteral(o: CstMacroLiteral) = true

    open fun visitMacroVar(o: CstMacroVar) = true

    open fun visitMacroVerbatim(o: CstMacroVerbatim) = true

    open fun visitMagicConstant(o: CstMagicConstant) = true

    open fun visitMetaclass(o: CstMetaclass) = true

    open fun visitModuleDef(o: CstModuleDef) = true

    open fun visitMultiAssign(o: CstMultiAssign) = true

    open fun visitNamedArgument(o: CstNamedArgument) = true

    open fun visitNamedTupleLiteral(o: CstNamedTupleLiteral) = true

    open fun visitNext(o: CstNext) = true

    open fun visitNilableCast(o: CstNilableCast) = true

    open fun visitNilLiteral(o: CstNilLiteral) = true

    open fun visitNop(o: CstNop) = true

    open fun visitNot(o: CstNot) = true

    open fun visitNumberLiteral(o: CstNumberLiteral) = true

    open fun visitOffsetOf(o: CstOffsetOf) = true

    open fun visitOpAssign(o: CstOpAssign) = true

    open fun visitOr(o: CstOr) = true

    open fun visitOut(o: CstOut) = true

    open fun visitPath(o: CstPath) = true

    open fun visitPointerOf(o: CstPointerOf) = true

    open fun visitProcLiteral(o: CstProcLiteral) = true

    open fun visitProcNotation(o: CstProcNotation) = true

    open fun visitProcPointer(o: CstProcPointer) = true

    open fun visitRangeLiteral(o: CstRangeLiteral) = true

    open fun visitReadInstanceVar(o: CstReadInstanceVar) = true

    open fun visitRegexLiteral(o: CstRegexLiteral) = true

    open fun visitRequire(o: CstRequire) = true

    open fun visitRescue(o: CstRescue) = true

    open fun visitRespondsTo(o: CstRespondsTo) = true

    open fun visitReturn(o: CstReturn) = true

    open fun visitSelect(o: CstSelect) = true

    open fun visitSelf(o: CstSelf) = true

    open fun visitSizeOf(o: CstSizeOf) = true

    open fun visitSplat(o: CstSplat) = true

    open fun visitStringInterpolation(o: CstStringInterpolation) = true

    open fun visitStringLiteral(o: CstStringLiteral) = true

    open fun visitSymbolLiteral(o: CstSymbolLiteral) = true

    open fun visitTupleLiteral(o: CstTupleLiteral) = true

    open fun visitTypeDeclaration(o: CstTypeDeclaration) = true

    open fun visitTypeDef(o: CstTypeDef) = true

    open fun visitTypeOf(o: CstTypeOf) = true

    open fun visitUnderscore(o: CstUnderscore) = true

    open fun visitUninitializedVar(o: CstUninitializedVar) = true

    open fun visitUnion(o: CstUnion) = true

    open fun visitUnless(o: CstUnless) = true

    open fun visitUntil(o: CstUntil) = true

    open fun visitVar(o: CstVar) = true

    open fun visitVisibilityModifier(o: CstVisibilityModifier) = true

    open fun visitWhen(o: CstWhen) = true

    open fun visitWhile(o: CstWhile) = true

    open fun visitYield(o: CstYield) = true
}
package org.crystal.intellij.lang.psi

import com.intellij.psi.PsiElementVisitor

open class CrVisitor : PsiElementVisitor() {
    open fun visitAlias(o: CrAlias) = visitDefinition(o)

    open fun visitAnnotation(o: CrAnnotation) = visitDefinition(o)

    open fun visitAnnotationExpression(o: CrAnnotationExpression) = visitExpression(o)

    open fun visitArgumentList(o: CrArgumentList) = visitCrElement(o)

    open fun visitArrayLiteralExpression(o: CrArrayLiteralExpression) = visitExpression(o)

    open fun visitAsExpression(o: CrAsExpression) = visitExpression(o)

    open fun visitAsmClobberList(o: CrAsmClobberList) = visitCrElement(o)

    open fun visitAsmExpression(o: CrAsmExpression) = visitExpression(o)

    open fun visitAsmOperand(o: CrAsmOperand) = visitCrElement(o)

    open fun visitAsmInList(o: CrAsmInList) = visitAsmOperandList(o)

    open fun visitAsmOperandList(o: CrAsmOperandList) = visitCrElement(o)

    open fun visitAsmOutList(o: CrAsmOutList) = visitAsmOperandList(o)

    open fun visitAsmOptionsList(o: CrAsmOptionsList) = visitCrElement(o)

    open fun visitAssignmentExpression(o: CrAssignmentExpression) = visitExpression(o)

    open fun visitBinaryExpression(o: CrBinaryExpression) = visitExpression(o)

    open fun visitBlockExpression(o: CrBlockExpression) = visitExpression(o)

    open fun visitBlockParameterList(o: CrBlockParameterList) = visitCrElement(o)

    open fun visitBody(o: CrBody) = visitCrElement(o)

    open fun visitBooleanLiteralExpression(o: CrBooleanLiteralExpression) = visitExpression(o)

    open fun visitBreakExpression(o: CrBreakExpression) = visitVoidExpression(o)

    open fun visitCallExpression(o: CrCallExpression) = visitExpression(o)

    open fun visitCaseExpression(o: CrCaseExpression) = visitExpression(o)

    open fun visitCharCodeElement(o: CrCharCodeElement) = visitCrElement(o)

    open fun visitCharLiteralExpression(o: CrCharLiteralExpression) = visitExpression(o)

    open fun visitCharRawElement(o: CrCharRawElement) = visitCrElement(o)

    open fun visitClass(o: CrClass) = visitDefinition(o)

    open fun visitCommandExpression(o: CrCommandExpression) = visitExpression(o)

    open fun visitConcatenatedStringLiteralExpression(o: CrConcatenatedStringLiteralExpression) = visitExpression(o)

    open fun visitConditionalExpression(o: CrConditionalExpression) = visitExpression(o)

    open fun visitConstant(o: CrConstant) = visitDefinition(o)

    open fun visitCrElement(o: CrElement) = visitElement(o)

    open fun visitCrFile(o: CrFile) = visitFile(o)

    open fun visitCField(o: CrCField) = visitDefinition(o)

    open fun visitCFieldGroup(o: CrCFieldGroup) = visitCrElement(o)

    open fun visitCStruct(o: CrCStruct) = visitDefinition(o)

    open fun visitCUnion(o: CrCUnion) = visitDefinition(o)

    open fun visitDefinition(o: CrDefinition) = visitExpression(o)

    open fun visitDoubleSplatExpression(o: CrDoubleSplatExpression) = visitExpression(o)

    open fun visitDoubleSplatType(o: CrDoubleSplatTypeElement) = visitType(o)

    open fun visitElseClause(o: CrElseClause) = visitCrElement(o)

    open fun visitEnsureClause(o: CrEnsureClause) = visitCrElement(o)

    open fun visitEnsureExpression(o: CrEnsureExpression) = visitExpression(o)

    open fun visitEnum(o: CrEnum) = visitDefinition(o)

    open fun visitEnumConstant(o: CrEnumConstant) = visitDefinition(o)

    open fun visitEscapeElement(o: CrEscapeElement) = visitCrElement(o)

    open fun visitExceptionHandler(o: CrExceptionHandler) = visitCrElement(o)

    open fun visitExpression(o: CrExpression) = visitCrElement(o)

    open fun visitExpressionType(o: CrExpressionTypeElement) = visitType(o)

    open fun visitExtendExpression(o: CrExtendExpression) = visitExpression(o)

    open fun visitFileFragment(o: CrFileFragment) = visitCrElement(o)

    open fun visitFloatLiteralExpression(o: CrFloatLiteralExpression) = visitExpression(o)

    open fun visitFunction(o: CrFunction) = visitDefinition(o)

    open fun visitFunctionLiteralExpression(o: CrFunctionLiteralExpression) = visitExpression(o)

    open fun visitFunctionPointerExpression(o: CrFunctionPointerExpression) = visitExpression(o)

    open fun visitGlobalMatchDataIndexName(o: CrGlobalMatchIndexName) = visitCrElement(o)

    open fun visitHashEntry(o: CrHashEntry) = visitCrElement(o)

    open fun visitHashExpression(o: CrHashExpression) = visitExpression(o)

    open fun visitHashType(o: CrHashTypeElement) = visitType(o)

    open fun visitHeredocEndId(o: CrHeredocEndId) = visitCrElement(o)

    open fun visitHeredocExpression(o: CrHeredocExpression) = visitExpression(o)

    open fun visitHeredocIndent(o: CrHeredocIndent) = visitCrElement(o)

    open fun visitHeredocLiteralBody(o: CrHeredocLiteralBody) = visitCrElement(o)

    open fun visitHeredocRawElement(o: CrHeredocRawElement) = visitCrElement(o)

    open fun visitHeredocStartId(o: CrHeredocStartId) = visitCrElement(o)

    open fun visitHexEscapeElement(o: CrHexEscapeElement) = visitEscapeElement(o)

    open fun visitIfExpression(o: CrIfExpression) = visitExpression(o)

    open fun visitIncludeExpression(o: CrIncludeExpression) = visitExpression(o)

    open fun visitIndexedExpression(o: CrIndexedExpression) = visitExpression(o)

    open fun visitInstanceSizeExpression(o: CrInstanceSizeExpression) = visitExpression(o)

    open fun visitInstantiatedType(o: CrInstantiatedTypeElement) = visitType(o)

    open fun visitIntegerLiteralExpression(o: CrIntegerLiteralExpression) = visitExpression(o)

    open fun visitIsExpression(o: CrIsExpression) = visitExpression(o)

    open fun visitIsNilExpression(o: CrIsNilExpression) = visitExpression(o)

    open fun visitLabeledType(o: CrLabeledTypeElement) = visitType(o)

    open fun visitLibrary(o: CrLibrary) = visitDefinition(o)

    open fun visitListExpression(o: CrListExpression) = visitExpression(o)

    open fun visitMacro(o: CrMacro) = visitDefinition(o)

    open fun visitMacroExpression(o: CrMacroExpression) = visitExpression(o)

    open fun visitMacroBlockStatement(o: CrMacroBlockStatement) = visitExpression(o)

    open fun visitMacroForStatement(o: CrMacroForStatement) = visitExpression(o)

    open fun visitMacroFragment(o: CrMacroFragment) = visitCrElement(o)

    open fun visitMacroIfStatement(o: CrMacroIfStatement) = visitExpression(o)

    open fun visitMacroLiteral(o: CrMacroLiteral) = visitCrElement(o)

    open fun visitMacroUnlessStatement(o: CrMacroUnlessStatement) = visitExpression(o)

    open fun visitMacroVariableExpression(o: CrMacroVariableExpression) = visitExpression(o)

    open fun visitMacroVerbatimStatement(o: CrMacroVerbatimStatement) = visitExpression(o)

    open fun visitMacroWrapperStatement(o: CrMacroWrapperStatement) = visitExpression(o)

    open fun visitMetaclassType(o: CrMetaclassTypeElement) = visitType(o)

    open fun visitMethod(o: CrMethod) = visitDefinition(o)

    open fun visitModule(o: CrModule) = visitDefinition(o)

    open fun visitMultiParameter(o: CrMultiParameter) = visitParameter(o)

    open fun visitNameElement(o: CrNameElement) = visitCrElement(o)

    open fun visitNamedArgument(o: CrNamedArgument) = visitCrElement(o)

    open fun visitNamedTupleEntry(o: CrNamedTupleEntry) = visitCrElement(o)

    open fun visitNamedTupleExpression(o: CrNamedTupleExpression) = visitExpression(o)

    open fun visitNamedTupleType(o: CrNamedTupleTypeElement) = visitType(o)

    open fun visitNextExpression(o: CrNextExpression) = visitVoidExpression(o)

    open fun visitNilableType(o: CrNilableTypeElement) = visitType(o)

    open fun visitNilExpression(o: CrNilExpression) = visitExpression(o)

    open fun visitNilableExpression(o: CrNilableExpression) = visitExpression(o)

    open fun visitOctalEscapeElement(o: CrOctalEscapeElement) = visitEscapeElement(o)

    open fun visitOffsetExpression(o: CrOffsetExpression) = visitExpression(o)

    open fun visitOutArgument(o: CrOutArgument) = visitCrElement(o)

    open fun visitParenthesizedExpression(o: CrParenthesizedExpression) = visitExpression(o)

    open fun visitParenthesizedType(o: CrParenthesizedTypeElement) = visitType(o)

    open fun visitParameter(o: CrParameter) = visitDefinition(o)

    open fun visitParameterList(o: CrParameterList) = visitCrElement(o)

    open fun visitPathNameElement(o: CrPathNameElement) = visitNameElement(o)

    open fun visitPathExpression(o: CrPathExpression) = visitExpression(o)

    open fun visitPathType(o: CrPathTypeElement) = visitType(o)

    open fun visitPointerExpression(o: CrPointerExpression) = visitExpression(o)

    open fun visitPointerType(o: CrPointerTypeElement) = visitType(o)

    open fun visitProcType(o: CrProcTypeElement) = visitType(o)

    open fun visitPseudoConstantExpression(o: CrPseudoConstantExpression) = visitExpression(o)

    open fun visitRawEscapeElement(o: CrRawEscapeElement) = visitEscapeElement(o)

    open fun visitReferenceExpression(o: CrReferenceExpression) = visitExpression(o)

    open fun visitRegexExpression(o: CrRegexExpression) = visitExpression(o)

    open fun visitRegexOptionsElement(o: CrRegexOptionsElement) = visitCrElement(o)

    open fun visitRequireExpression(o: CrRequireExpression) = visitExpression(o)

    open fun visitRescueClause(o: CrRescueClause) = visitCrElement(o)

    open fun visitRescueExpression(o: CrRescueExpression) = visitExpression(o)

    open fun visitRespondsToExpression(o: CrRespondsToExpression) = visitExpression(o)

    open fun visitReturnExpression(o: CrReturnExpression) = visitVoidExpression(o)

    open fun visitSelectExpression(o: CrSelectExpression) = visitExpression(o)

    open fun visitSelfType(o: CrSelfTypeElement) = visitType(o)

    open fun visitShortBlockArgument(o: CrShortBlockArgument) = visitCrElement(o)

    open fun visitSimpleEscapeElement(o: CrSimpleEscapeElement) = visitEscapeElement(o)

    open fun visitSimpleNameElement(o: CrSimpleNameElement) = visitNameElement(o)

    open fun visitSimpleParameter(o: CrSimpleParameter) = visitParameter(o)

    open fun visitSizeExpression(o: CrSizeExpression) = visitExpression(o)

    open fun visitSpecialEscapeElement(o: CrSpecialEscapeElement) = visitEscapeElement(o)

    open fun visitSplatExpression(o: CrSplatExpression) = visitExpression(o)

    open fun visitSplatType(o: CrSplatTypeElement) = visitType(o)

    open fun visitStaticArrayType(o: CrStaticArrayTypeElement) = visitType(o)

    open fun visitStringArrayExpression(o: CrStringArrayExpression) = visitExpression(o)

    open fun visitStringInterpolation(o: CrStringInterpolation) = visitCrElement(o)

    open fun visitStringLiteralExpression(o: CrStringLiteralExpression) = visitExpression(o)

    open fun visitStringRawElement(o: CrStringRawElement) = visitCrElement(o)

    open fun visitStruct(o: CrStruct) = visitDefinition(o)

    open fun visitSupertypeClause(o: CrSupertypeClause) = visitCrElement(o)

    open fun visitSymbolArrayExpression(o: CrSymbolArrayExpression) = visitExpression(o)

    open fun visitSymbolExpression(o: CrSymbolExpression) = visitExpression(o)

    open fun visitSyntheticArg(o: CrSyntheticArg) = visitCrElement(o)

    open fun visitThenClause(o: CrThenClause) = visitCrElement(o)

    open fun visitTupleExpression(o: CrTupleExpression) = visitExpression(o)

    open fun visitTupleType(o: CrTupleTypeElement) = visitType(o)

    open fun visitType(o: CrTypeElement<*>) = visitCrElement(o)

    open fun visitTypeArgumentList(o: CrTypeArgumentList) = visitCrElement(o)

    open fun visitTypeDef(o: CrTypeDef) = visitDefinition(o)

    open fun visitTypeExpression(o: CrTypeExpression) = visitExpression(o)

    open fun visitTypeofExpression(o: CrTypeofExpression) = visitExpression(o)

    open fun visitTypeParameter(o: CrTypeParameter) = visitDefinition(o)

    open fun visitTypeParameterList(o: CrTypeParameterList) = visitCrElement(o)

    open fun visitUnaryExpression(o: CrUnaryExpression) = visitExpression(o)

    open fun visitUnderscoreType(o: CrUnderscoreTypeElement) = visitType(o)

    open fun visitUnicodeBlock(o: CrUnicodeBlock) = visitCrElement(o)

    open fun visitUnicodeEscapeElement(o: CrUnicodeEscapeElement) = visitEscapeElement(o)

    open fun visitUninitializedExpression(o: CrUninitializedExpression) = visitExpression(o)

    open fun visitUnionType(o: CrUnionTypeElement) = visitType(o)

    open fun visitUnlessExpression(o: CrUnlessExpression) = visitExpression(o)

    open fun visitUntilExpression(o: CrUntilExpression) = visitExpression(o)

    open fun visitVariable(o: CrVariable) = visitDefinition(o)

    open fun visitVisibilityModifier(o: CrVisibilityModifier) = visitCrElement(o)

    open fun visitVoidExpression(o: CrVoidExpression) = visitExpression(o)

    open fun visitWhenClause(o: CrWhenClause) = visitCrElement(o)

    open fun visitWhileExpression(o: CrWhileExpression) = visitExpression(o)

    open fun visitYieldExpression(o: CrYieldExpression) = visitExpression(o)
}
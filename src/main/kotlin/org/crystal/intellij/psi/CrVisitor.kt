package org.crystal.intellij.psi

import com.intellij.psi.PsiElementVisitor

open class CrVisitor : PsiElementVisitor() {
    open fun visitAlias(o: CrAlias) = visitDefinition(o)

    open fun visitAnnotation(o: CrAnnotation) = visitDefinition(o)

    open fun visitAnnotationExpression(o: CrAnnotationExpression) = visitExpression(o)

    open fun visitArgumentList(o: CrArgumentList) = visitCrElement(o)

    open fun visitArrayLiteralExpression(o: CrArrayLiteralExpression) = visitExpression(o)

    open fun visitAsExpression(o: CrAsExpression) = visitExpression(o)
    
    open fun visitAssignmentExpression(o: CrAssignmentExpression) = visitExpression(o)

    open fun visitBinaryExpression(o: CrBinaryExpression) = visitExpression(o)

    open fun visitBlockExpression(o: CrBlockExpression) = visitExpression(o)

    open fun visitBodyClause(o: CrBody) = visitCrElement(o)

    open fun visitBooleanLiteralExpression(o: CrBooleanLiteralExpression) = visitExpression(o)

    open fun visitBreakExpression(o: CrBreakExpression) = visitExpression(o)

    open fun visitCallExpression(o: CrCallExpression) = visitExpression(o)

    open fun visitCaseExpression(o: CrCaseExpression) = visitExpression(o)

    open fun visitCharCodeElement(o: CrCharCodeElement) = visitCrElement(o)

    open fun visitCharLiteralExpression(o: CrCharLiteralExpression) = visitExpression(o)

    open fun visitClass(o: CrClass) = visitDefinition(o)

    open fun visitCommandExpression(o: CrCommandExpression) = visitExpression(o)

    open fun visitConcatenatedStringLiteralExpression(o: CrConcatenatedStringLiteralExpression) = visitExpression(o)

    open fun visitConditionalExpression(o: CrConditionalExpression) = visitExpression(o)

    open fun visitCrElement(o: CrElement) = visitElement(o)

    open fun visitCField(o: CrCField) = visitDefinition(o)

    open fun visitCFieldGroup(o: CrCFieldGroup) = visitCrElement(o)

    open fun visitCStruct(o: CrCStruct) = visitDefinition(o)

    open fun visitCUnion(o: CrCUnion) = visitDefinition(o)

    open fun visitDefinition(o: CrDefinition) = visitExpression(o)

    open fun visitDoubleSplatExpression(o: CrDoubleSplatExpression) = visitExpression(o)

    open fun visitDoubleSplatType(o: CrDoubleSplatType) = visitType(o)

    open fun visitElseClause(o: CrElseClause) = visitCrElement(o)

    open fun visitEnsureClause(o: CrEnsureClause) = visitCrElement(o)

    open fun visitEnsureExpression(o: CrEnsureExpression) = visitExpression(o)

    open fun visitEnum(o: CrEnum) = visitDefinition(o)

    open fun visitEnumConstant(o: CrEnumConstant) = visitDefinition(o)

    open fun visitEscapeElement(o: CrEscapeElement) = visitCrElement(o)

    open fun visitExceptionHandler(o: CrExceptionHandler) = visitCrElement(o)

    open fun visitExpression(o: CrExpression) = visitCrElement(o)

    open fun visitExpressionType(o: CrExpressionType) = visitType(o)

    open fun visitExtendExpression(o: CrExtendExpression) = visitExpression(o)

    open fun visitFloatLiteralExpression(o: CrFloatLiteralExpression) = visitExpression(o)

    open fun visitFunction(o: CrFunction) = visitDefinition(o)

    open fun visitFunctionLiteralExpression(o: CrFunctionLiteralExpression) = visitExpression(o)

    open fun visitFunctionPointerExpression(o: CrFunctionPointerExpression) = visitExpression(o)

    open fun visitHashEntry(o: CrHashEntry) = visitCrElement(o)

    open fun visitHashExpression(o: CrHashExpression) = visitExpression(o)

    open fun visitHashType(o: CrHashType) = visitType(o)

    open fun visitHeredocLiteralBody(o: CrHeredocLiteralBody) = visitCrElement(o)

    open fun visitHeredocExpression(o: CrHeredocExpression) = visitExpression(o)

    open fun visitHexEscapeElement(o: CrHexEscapeElement) = visitEscapeElement(o)

    open fun visitIfExpression(o: CrIfExpression) = visitExpression(o)

    open fun visitIncludeExpression(o: CrIncludeExpression) = visitExpression(o)

    open fun visitIndexedExpression(o: CrIndexedExpression) = visitExpression(o)

    open fun visitInstanceSizeExpression(o: CrInstanceSizeExpression) = visitExpression(o)

    open fun visitInstantiatedType(o: CrInstantiatedType) = visitType(o)

    open fun visitIntegerLiteralExpression(o: CrIntegerLiteralExpression) = visitExpression(o)

    open fun visitIsExpression(o: CrIsExpression) = visitExpression(o)

    open fun visitIsNilExpression(o: CrIsNilExpression) = visitExpression(o)

    open fun visitLabeledType(o: CrLabeledType) = visitType(o)

    open fun visitLibrary(o: CrLibrary) = visitDefinition(o)

    open fun visitListExpression(o: CrListExpression) = visitExpression(o)

    open fun visitMetaclassType(o: CrMetaclassType) = visitType(o)

    open fun visitMethod(o: CrMethod) = visitDefinition(o)

    open fun visitModule(o: CrModule) = visitDefinition(o)

    open fun visitMultiParameter(o: CrMultiParameter) = visitParameter(o)

    open fun visitNameElement(o: CrNameElement) = visitCrElement(o)

    open fun visitNamedArgumentExpression(o: CrNamedArgumentExpression) = visitExpression(o)

    open fun visitNamedTupleEntry(o: CrNamedTupleEntry) = visitCrElement(o)

    open fun visitNamedTupleExpression(o: CrNamedTupleExpression) = visitExpression(o)

    open fun visitNamedTupleType(o: CrNamedTupleType) = visitType(o)

    open fun visitNextExpression(o: CrNextExpression) = visitExpression(o)

    open fun visitNilableType(o: CrNilableType) = visitType(o)

    open fun visitNilExpression(o: CrNilExpression) = visitExpression(o)

    open fun visitNilableExpression(o: CrNilableExpression) = visitExpression(o)

    open fun visitOctalEscapeElement(o: CrOctalEscapeElement) = visitEscapeElement(o)

    open fun visitOffsetExpression(o: CrOffsetExpression) = visitExpression(o)

    open fun visitOutArgumentExpression(o: CrOutArgumentExpression) = visitExpression(o)

    open fun visitParenthesizedExpression(o: CrParenthesizedExpression) = visitExpression(o)

    open fun visitParenthesizedType(o: CrParenthesizedType) = visitType(o)

    open fun visitParameter(o: CrParameter) = visitDefinition(o)

    open fun visitParameterList(o: CrParameterList) = visitCrElement(o)

    open fun visitPathNameElement(o: CrPathNameElement) = visitNameElement(o)

    open fun visitPathExpression(o: CrPathExpression) = visitExpression(o)

    open fun visitPathType(o: CrPathType) = visitType(o)

    open fun visitPointerExpression(o: CrPointerExpression) = visitExpression(o)

    open fun visitPointerType(o: CrPointerType) = visitType(o)

    open fun visitProcType(o: CrProcType) = visitType(o)

    open fun visitPseudoConstantExpression(o: CrPseudoConstantExpression) = visitExpression(o)

    open fun visitRangeExpression(o: CrRangeExpression) = visitExpression(o)

    open fun visitReferenceExpression(o: CrReferenceExpression) = visitExpression(o)

    open fun visitRegexExpression(o: CrRegexExpression) = visitExpression(o)

    open fun visitRequireExpression(o: CrRequireExpression) = visitExpression(o)

    open fun visitRescueClause(o: CrRescueClause) = visitCrElement(o)

    open fun visitRescueExpression(o: CrRescueExpression) = visitExpression(o)

    open fun visitRespondsToExpression(o: CrRespondsToExpression) = visitExpression(o)

    open fun visitReturnExpression(o: CrReturnExpression) = visitExpression(o)

    open fun visitSelfExpression(o: CrSelfExpression) = visitExpression(o)

    open fun visitSelectExpression(o: CrSelectExpression) = visitExpression(o)

    open fun visitSelfType(o: CrSelfType) = visitType(o)

    open fun visitShortBlockExpression(o: CrShortBlockExpression) = visitExpression(o)

    open fun visitSimpleEscapeElement(o: CrSimpleEscapeElement) = visitEscapeElement(o)

    open fun visitSimpleNameElement(o: CrSimpleNameElement) = visitNameElement(o)

    open fun visitSimpleParameter(o: CrSimpleParameter) = visitParameter(o)

    open fun visitSizeExpression(o: CrSizeExpression) = visitExpression(o)

    open fun visitSplatExpression(o: CrSplatExpression) = visitExpression(o)

    open fun visitSplatType(o: CrSplatType) = visitType(o)

    open fun visitStaticArrayType(o: CrStaticArrayType) = visitType(o)

    open fun visitStringArrayExpression(o: CrStringArrayExpression) = visitExpression(o)

    open fun visitStringInterpolation(o: CrStringInterpolation) = visitCrElement(o)

    open fun visitStringLiteralExpression(o: CrStringLiteralExpression) = visitExpression(o)

    open fun visitStruct(o: CrStruct) = visitDefinition(o)

    open fun visitSupertypeClause(o: CrSupertypeClause) = visitCrElement(o)

    open fun visitSymbolArrayExpression(o: CrSymbolArrayExpression) = visitExpression(o)

    open fun visitSymbolExpression(o: CrSymbolExpression) = visitExpression(o)

    open fun visitThenClause(o: CrThenClause) = visitCrElement(o)

    open fun visitTupleExpression(o: CrTupleExpression) = visitExpression(o)

    open fun visitTupleType(o: CrTupleType) = visitType(o)

    open fun visitType(o: CrType) = visitCrElement(o)

    open fun visitTypeArgumentList(o: CrTypeArgumentList) = visitCrElement(o)

    open fun visitTypeDef(o: CrTypeDef) = visitDefinition(o)

    open fun visitTypeExpression(o: CrTypeExpression) = visitExpression(o)

    open fun visitTypeParameter(o: CrTypeParameter) = visitDefinition(o)

    open fun visitTypeParameterList(o: CrTypeParameterList) = visitCrElement(o)

    open fun visitUnaryExpression(o: CrUnaryExpression) = visitExpression(o)

    open fun visitUnderscoreType(o: CrUnderscoreType) = visitType(o)

    open fun visitUnicodeBlock(o: CrUnicodeBlock) = visitCrElement(o)

    open fun visitUnicodeEscapeElement(o: CrUnicodeEscapeElement) = visitEscapeElement(o)

    open fun visitUninitializedExpression(o: CrUninitializedExpression) = visitExpression(o)

    open fun visitUnionType(o: CrUnionType) = visitType(o)

    open fun visitUnlessExpression(o: CrUnlessExpression) = visitExpression(o)

    open fun visitUntilExpression(o: CrUntilExpression) = visitExpression(o)

    open fun visitVariable(o: CrVariable) = visitDefinition(o)

    open fun visitVariadicParameter(o: CrVariadicParameter) = visitParameter(o)

    open fun visitVisibilityExpression(o: CrVisibilityExpression) = visitExpression(o)

    open fun visitWithExpression(o: CrWithExpression) = visitExpression(o)

    open fun visitWhenClause(o: CrWhenClause) = visitCrElement(o)

    open fun visitWhileExpression(o: CrWhileExpression) = visitExpression(o)

    open fun visitYieldExpression(o: CrYieldExpression) = visitExpression(o)
}
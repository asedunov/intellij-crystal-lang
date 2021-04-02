package org.crystal.intellij.psi

import com.intellij.psi.PsiElementVisitor

open class CrVisitor : PsiElementVisitor() {
    fun visitAlias(o: CrAlias) = visitDefinition(o)

    fun visitAnnotation(o: CrAnnotation) = visitDefinition(o)

    fun visitAnnotationExpression(o: CrAnnotationExpression) = visitExpression(o)

    fun visitArgumentList(o: CrArgumentList) = visitCrElement(o)

    fun visitArrayLiteralExpression(o: CrArrayLiteralExpression) = visitExpression(o)

    fun visitAsExpression(o: CrAsExpression) = visitExpression(o)
    
    fun visitAssignmentExpression(o: CrAssignmentExpression) = visitExpression(o)

    fun visitBinaryExpression(o: CrBinaryExpression) = visitExpression(o)

    fun visitBlockExpression(o: CrBlockExpression) = visitExpression(o)

    fun visitBodyClause(o: CrBodyClause) = visitCrElement(o)

    fun visitBooleanLiteralExpression(o: CrBooleanLiteralExpression) = visitExpression(o)

    fun visitBreakExpression(o: CrBreakExpression) = visitExpression(o)

    fun visitCallExpression(o: CrCallExpression) = visitExpression(o)

    fun visitCaseExpression(o: CrCaseExpression) = visitExpression(o)

    fun visitCharLiteralExpression(o: CrCharLiteralExpression) = visitExpression(o)

    fun visitClass(o: CrClass) = visitDefinition(o)

    fun visitCommandExpression(o: CrCommandExpression) = visitExpression(o)

    fun visitConcatenatedStringLiteralExpression(o: CrConcatenatedStringLiteralExpression) = visitExpression(o)

    fun visitConditionalExpression(o: CrConditionalExpression) = visitExpression(o)

    fun visitCrElement(o: CrElement) = visitElement(o)

    fun visitCField(o: CrCField) = visitDefinition(o)

    fun visitCFieldGroup(o: CrCFieldGroup) = visitCrElement(o)

    fun visitCStruct(o: CrCStruct) = visitDefinition(o)

    fun visitCUnion(o: CrCUnion) = visitDefinition(o)

    fun visitDefinition(o: CrDefinition) = visitExpression(o)

    fun visitDoubleSplatExpression(o: CrDoubleSplatExpression) = visitExpression(o)

    fun visitDoubleSplatType(o: CrDoubleSplatType) = visitType(o)

    fun visitElseClause(o: CrElseClause) = visitCrElement(o)

    fun visitEnsureClause(o: CrEnsureClause) = visitCrElement(o)

    fun visitEnsureExpression(o: CrEnsureExpression) = visitExpression(o)

    fun visitEnum(o: CrEnum) = visitDefinition(o)

    fun visitEnumConstant(o: CrEnumConstant) = visitDefinition(o)

    fun visitExceptionHandler(o: CrExceptionHandler) = visitCrElement(o)

    fun visitExpression(o: CrExpression) = visitCrElement(o)

    fun visitExpressionType(o: CrExpressionType) = visitType(o)

    fun visitExtendExpression(o: CrExtendExpression) = visitExpression(o)

    fun visitFunction(o: CrFunction) = visitDefinition(o)

    fun visitFunctionLiteralExpression(o: CrFunctionLiteralExpression) = visitExpression(o)

    fun visitFunctionPointerExpression(o: CrFunctionPointerExpression) = visitExpression(o)

    fun visitHashEntry(o: CrHashEntry) = visitCrElement(o)

    fun visitHashExpression(o: CrHashExpression) = visitExpression(o)

    fun visitHashType(o: CrHashType) = visitType(o)

    fun visitHeredocLiteralBody(o: CrHeredocLiteralBody) = visitCrElement(o)

    fun visitHeredocExpression(o: CrHeredocExpression) = visitExpression(o)

    fun visitIfExpression(o: CrIfExpression) = visitExpression(o)

    fun visitIncludeExpression(o: CrIncludeExpression) = visitExpression(o)

    fun visitIndexedExpression(o: CrIndexedExpression) = visitExpression(o)

    fun visitIndexedLHSExpression(o: CrIndexedLHSExpression) = visitExpression(o)

    fun visitInstanceSizeExpression(o: CrInstanceSizeExpression) = visitExpression(o)

    fun visitInstantiatedType(o: CrInstantiatedType) = visitType(o)

    fun visitIsExpression(o: CrIsExpression) = visitExpression(o)

    fun visitIsNilExpression(o: CrIsNilExpression) = visitExpression(o)

    fun visitLabeledType(o: CrLabeledType) = visitType(o)

    fun visitLibrary(o: CrLibrary) = visitDefinition(o)

    fun visitListExpression(o: CrListExpression) = visitExpression(o)

    fun visitMetaclassType(o: CrMetaclassType) = visitType(o)

    fun visitMethod(o: CrMethod) = visitDefinition(o)

    fun visitModule(o: CrModule) = visitDefinition(o)

    fun visitMultiParameter(o: CrMultiParameter) = visitParameter(o)

    fun visitNameElement(o: CrNameElement) = visitCrElement(o)

    fun visitNamedArgumentExpression(o: CrNamedArgumentExpression) = visitExpression(o)

    fun visitNamedTupleEntry(o: CrNamedTupleEntry) = visitCrElement(o)

    fun visitNamedTupleExpression(o: CrNamedTupleExpression) = visitExpression(o)

    fun visitNamedTupleType(o: CrNamedTupleType) = visitType(o)

    fun visitNextExpression(o: CrNextExpression) = visitExpression(o)

    fun visitNilableType(o: CrNilableType) = visitType(o)

    fun visitNilExpression(o: CrNilExpression) = visitExpression(o)

    fun visitNilableExpression(o: CrNilableExpression) = visitExpression(o)

    fun visitNumericLiteralExpression(o: CrNumericLiteralExpression) = visitExpression(o)

    fun visitOffsetExpression(o: CrOffsetExpression) = visitExpression(o)

    fun visitOutArgumentExpression(o: CrOutArgumentExpression) = visitExpression(o)

    fun visitParenthesizedExpression(o: CrParenthesizedExpression) = visitExpression(o)

    fun visitParenthesizedType(o: CrParenthesizedType) = visitType(o)

    fun visitParameter(o: CrParameter) = visitDefinition(o)

    fun visitParameterList(o: CrParameterList) = visitCrElement(o)

    fun visitPath(o: CrPath) = visitCrElement(o)

    fun visitPathExpression(o: CrPathExpression) = visitExpression(o)

    fun visitPathType(o: CrPathType) = visitType(o)

    fun visitPointerExpression(o: CrPointerExpression) = visitExpression(o)

    fun visitPointerType(o: CrPointerType) = visitType(o)

    fun visitProcType(o: CrProcType) = visitType(o)

    fun visitPseudoConstantExpression(o: CrPseudoConstantExpression) = visitExpression(o)

    fun visitRangeExpression(o: CrRangeExpression) = visitExpression(o)

    fun visitReferenceExpression(o: CrReferenceExpression) = visitExpression(o)

    fun visitRegexExpression(o: CrRegexExpression) = visitExpression(o)

    fun visitRequireExpression(o: CrRequireExpression) = visitExpression(o)

    fun visitRescueClause(o: CrRescueClause) = visitCrElement(o)

    fun visitRescueExpression(o: CrRescueExpression) = visitExpression(o)

    fun visitRespondsToExpression(o: CrRespondsToExpression) = visitExpression(o)

    fun visitReturnExpression(o: CrReturnExpression) = visitExpression(o)

    fun visitSelfExpression(o: CrSelfExpression) = visitExpression(o)

    fun visitSelectExpression(o: CrSelectExpression) = visitExpression(o)

    fun visitSelfType(o: CrSelfType) = visitType(o)

    fun visitShortBlockExpression(o: CrShortBlockExpression) = visitExpression(o)

    fun visitSimpleParameter(o: CrSimpleParameter) = visitParameter(o)

    fun visitSizeExpression(o: CrSizeExpression) = visitExpression(o)

    fun visitSplatExpression(o: CrSplatExpression) = visitExpression(o)

    fun visitSplatType(o: CrSplatType) = visitType(o)

    fun visitStaticArrayType(o: CrStaticArrayType) = visitType(o)

    fun visitStringArrayExpression(o: CrStringArrayExpression) = visitExpression(o)

    fun visitStringInterpolation(o: CrStringInterpolation) = visitCrElement(o)

    fun visitStringLiteralExpression(o: CrStringLiteralExpression) = visitExpression(o)

    fun visitStruct(o: CrStruct) = visitDefinition(o)

    fun visitSupertypeClause(o: CrSupertypeClause) = visitCrElement(o)

    fun visitSymbolArrayExpression(o: CrSymbolArrayExpression) = visitExpression(o)

    fun visitSymbolExpression(o: CrSymbolExpression) = visitExpression(o)

    fun visitThenClause(o: CrThenClause) = visitCrElement(o)

    fun visitTupleExpression(o: CrTupleExpression) = visitExpression(o)

    fun visitTupleType(o: CrTupleType) = visitType(o)

    fun visitType(o: CrType) = visitCrElement(o)

    fun visitTypeArgumentList(o: CrTypeArgumentList) = visitCrElement(o)

    fun visitTypeBody(o: CrTypeBody) = visitCrElement(o)

    fun visitTypeDef(o: CrTypeDef) = visitDefinition(o)

    fun visitTypeExpression(o: CrTypeExpression) = visitExpression(o)

    fun visitTypeParameter(o: CrTypeParameter) = visitDefinition(o)

    fun visitTypeParameterList(o: CrTypeParameterList) = visitCrElement(o)

    fun visitUnaryExpression(o: CrUnaryExpression) = visitExpression(o)

    fun visitUnderscoreType(o: CrUnderscoreType) = visitType(o)

    fun visitUnicodeBlock(o: CrUnicodeBlock) = visitCrElement(o)

    fun visitUninitializedExpression(o: CrUninitializedExpression) = visitExpression(o)

    fun visitUnionType(o: CrUnionType) = visitType(o)

    fun visitUnlessExpression(o: CrUnlessExpression) = visitExpression(o)

    fun visitUntilExpression(o: CrUntilExpression) = visitExpression(o)

    fun visitVariable(o: CrVariable) = visitDefinition(o)

    fun visitVariadicParameter(o: CrVariadicParameter) = visitParameter(o)

    fun visitVisibilityExpression(o: CrVisibilityExpression) = visitExpression(o)

    fun visitWithExpression(o: CrWithExpression) = visitExpression(o)

    fun visitWhenClause(o: CrWhenClause) = visitCrElement(o)

    fun visitWhileExpression(o: CrWhileExpression) = visitExpression(o)

    fun visitYieldExpression(o: CrYieldExpression) = visitExpression(o)
}
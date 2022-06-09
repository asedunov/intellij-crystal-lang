package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.resolve.symbols.CrSym
import org.crystal.intellij.parser.CR_ANNOTATION_EXPRESSION
import org.crystal.intellij.stubs.api.CrAnnotationExpressionStub

class CrAnnotationExpression : CrStubbedElementImpl<CrAnnotationExpressionStub>, CrExpression {
    constructor(stub: CrAnnotationExpressionStub) : super(stub, CR_ANNOTATION_EXPRESSION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitAnnotationExpression(this)

    val path: CrPathNameElement?
        get() = childOfType()

    val targetSymbol: CrSym<*>?
        get() = path?.resolveSymbol()
}
package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrFunctionLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitFunctionLiteralExpression(this)

    val parameterList: CrParameterList?
        get() = childOfType()

    val body: CrBlockExpression?
        get() = childOfType()

    val returnType: CrTypeElement<*>?
        get() = childOfType()
}
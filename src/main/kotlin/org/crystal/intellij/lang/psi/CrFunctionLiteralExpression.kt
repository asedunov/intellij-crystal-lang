package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrFunctionLiteralExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression {
    override fun accept(visitor: CrVisitor) = visitor.visitFunctionLiteralExpression(this)

    val parameterList: CrParameterList?
        get() = childOfType()
}
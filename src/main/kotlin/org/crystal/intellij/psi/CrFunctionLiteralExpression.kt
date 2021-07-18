package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrFunctionLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitFunctionLiteralExpression(this)

    val parameterList: CrParameterList?
        get() = childOfType()
}
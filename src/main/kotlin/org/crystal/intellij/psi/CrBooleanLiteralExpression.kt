package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrBooleanLiteralExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression {
    override fun accept(visitor: CrVisitor) = visitor.visitBooleanLiteralExpression(this)
}
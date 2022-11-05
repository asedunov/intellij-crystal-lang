package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCharLiteralExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression {
    override fun accept(visitor: CrVisitor) = visitor.visitCharLiteralExpression(this)
}
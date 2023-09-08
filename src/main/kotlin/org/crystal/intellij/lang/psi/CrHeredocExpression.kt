package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrHeredocExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression {
    override fun accept(visitor: CrVisitor) = visitor.visitHeredocExpression(this)
}
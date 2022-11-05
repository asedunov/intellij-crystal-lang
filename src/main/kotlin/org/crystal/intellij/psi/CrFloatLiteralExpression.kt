package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrFloatLiteralExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression {
    override fun accept(visitor: CrVisitor) = visitor.visitFloatLiteralExpression(this)
}
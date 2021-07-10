package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrFloatLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitFloatLiteralExpression(this)
}
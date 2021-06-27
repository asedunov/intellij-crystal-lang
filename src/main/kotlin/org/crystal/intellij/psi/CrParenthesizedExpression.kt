package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrParenthesizedExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitParenthesizedExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIntegerLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIntegerLiteralExpression(this)
}
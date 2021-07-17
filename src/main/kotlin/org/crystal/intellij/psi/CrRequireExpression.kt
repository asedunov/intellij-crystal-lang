package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrRequireExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRequireExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIfExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIfExpression(this)
}
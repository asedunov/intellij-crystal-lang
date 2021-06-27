package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrShortBlockExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitShortBlockExpression(this)
}
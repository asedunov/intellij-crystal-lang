package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrVisibilityExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitVisibilityExpression(this)
}
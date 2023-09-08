package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrRescueExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRescueExpression(this)
}
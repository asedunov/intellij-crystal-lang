package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrRescueExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRescueExpression(this)
}
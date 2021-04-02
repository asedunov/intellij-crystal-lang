package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrBreakExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBreakExpression(this)
}
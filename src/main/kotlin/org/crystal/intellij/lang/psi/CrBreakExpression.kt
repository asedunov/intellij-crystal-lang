package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrBreakExpression(node: ASTNode) : CrVoidExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBreakExpression(this)
}
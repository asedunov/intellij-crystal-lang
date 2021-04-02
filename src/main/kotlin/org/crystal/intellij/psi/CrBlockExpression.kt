package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrBlockExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBlockExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIfExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIfExpression(this)
}
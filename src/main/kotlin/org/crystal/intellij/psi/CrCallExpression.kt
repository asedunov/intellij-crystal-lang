package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCallExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCallExpression(this)
}
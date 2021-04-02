package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrReturnExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitReturnExpression(this)
}
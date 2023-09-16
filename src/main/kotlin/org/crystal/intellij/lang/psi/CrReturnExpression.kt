package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrReturnExpression(node: ASTNode) : CrVoidExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitReturnExpression(this)
}
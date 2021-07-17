package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrHashExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHashExpression(this)
}
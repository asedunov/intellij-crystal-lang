package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrYieldExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitYieldExpression(this)
}
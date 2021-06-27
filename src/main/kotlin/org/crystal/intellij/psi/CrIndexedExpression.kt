package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIndexedExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIndexedExpression(this)
}
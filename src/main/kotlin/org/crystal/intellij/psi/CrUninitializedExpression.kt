package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrUninitializedExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUninitializedExpression(this)
}
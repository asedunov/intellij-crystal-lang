package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrUntilExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUntilExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrConditionalExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitConditionalExpression(this)
}
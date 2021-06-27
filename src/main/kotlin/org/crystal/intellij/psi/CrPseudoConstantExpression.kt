package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrPseudoConstantExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPseudoConstantExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrPseudoConstantExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPseudoConstantExpression(this)
}
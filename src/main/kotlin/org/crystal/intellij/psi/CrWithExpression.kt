package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrWithExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitWithExpression(this)
}
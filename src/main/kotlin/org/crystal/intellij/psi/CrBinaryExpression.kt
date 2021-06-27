package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrBinaryExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBinaryExpression(this)
}
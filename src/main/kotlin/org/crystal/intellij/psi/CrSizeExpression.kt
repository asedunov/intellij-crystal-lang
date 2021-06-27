package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSizeExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSizeExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrInstanceSizeExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitInstanceSizeExpression(this)
}
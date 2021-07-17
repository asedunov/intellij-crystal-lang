package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrOffsetExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitOffsetExpression(this)
}
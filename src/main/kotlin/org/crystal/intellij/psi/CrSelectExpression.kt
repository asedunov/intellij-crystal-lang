package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSelectExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSelectExpression(this)
}
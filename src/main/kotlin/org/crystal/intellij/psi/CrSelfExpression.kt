package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSelfExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSelfExpression(this)
}
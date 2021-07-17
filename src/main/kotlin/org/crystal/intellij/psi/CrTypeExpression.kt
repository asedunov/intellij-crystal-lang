package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrTypeExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeExpression(this)
}
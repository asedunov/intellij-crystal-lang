package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNamedArgumentExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedArgumentExpression(this)
}
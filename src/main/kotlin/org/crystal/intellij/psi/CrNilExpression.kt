package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNilExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNilExpression(this)
}
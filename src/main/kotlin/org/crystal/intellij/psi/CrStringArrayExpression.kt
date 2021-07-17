package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrStringArrayExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitStringArrayExpression(this)
}
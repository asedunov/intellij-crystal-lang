package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrStringArrayExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitStringArrayExpression(this)
}
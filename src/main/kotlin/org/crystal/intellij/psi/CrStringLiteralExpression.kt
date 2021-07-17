package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrStringLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitStringLiteralExpression(this)
}
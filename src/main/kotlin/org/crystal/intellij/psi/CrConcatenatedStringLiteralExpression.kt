package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrConcatenatedStringLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitConcatenatedStringLiteralExpression(this)
}
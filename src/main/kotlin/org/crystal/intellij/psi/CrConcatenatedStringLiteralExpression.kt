package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrConcatenatedStringLiteralExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitConcatenatedStringLiteralExpression(this)
}
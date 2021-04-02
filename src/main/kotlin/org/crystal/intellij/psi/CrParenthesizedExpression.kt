package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrParenthesizedExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitParenthesizedExpression(this)
}
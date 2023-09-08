package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrParenthesizedExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitParenthesizedExpression(this)

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()
}
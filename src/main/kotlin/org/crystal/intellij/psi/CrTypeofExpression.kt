package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrTypeofExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeofExpression(this)

    val expression: CrExpression?
        get() = childOfType()
}
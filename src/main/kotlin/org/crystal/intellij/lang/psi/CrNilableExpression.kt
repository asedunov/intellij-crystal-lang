package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrNilableExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNilableExpression(this)

    val argument: CrExpression?
        get() = childOfType()
}
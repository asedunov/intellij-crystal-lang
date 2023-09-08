package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrPointerExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPointerExpression(this)

    val argument: CrExpression?
        get() = childOfType()
}
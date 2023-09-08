package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrUninitializedExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUninitializedExpression(this)

    val type: CrTypeElement<*>?
        get() = childOfType()
}
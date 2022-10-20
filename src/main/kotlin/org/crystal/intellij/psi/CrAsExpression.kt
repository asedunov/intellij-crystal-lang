package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAsExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsExpression(this)

    val typeElement: CrTypeElement<*>?
        get() = childOfType()
}
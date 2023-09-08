package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrPathExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPathExpression(this)

    val nameElement: CrPathNameElement?
        get() = childOfType()
}
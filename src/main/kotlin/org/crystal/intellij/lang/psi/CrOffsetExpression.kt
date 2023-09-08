package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrOffsetExpression(node: ASTNode) : CrExpressionImpl(node), CrTypeArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitOffsetExpression(this)

    val type: CrTypeElement<*>?
        get() = childOfType()

    val offset: CrExpression?
        get() = childOfType()
}
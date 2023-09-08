package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrSizeExpression(node: ASTNode) : CrExpressionImpl(node), CrTypeArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitSizeExpression(this)

    val typeElement: CrTypeElement<*>?
        get() = childOfType()
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrInstanceSizeExpression(node: ASTNode) : CrExpressionImpl(node), CrTypeArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitInstanceSizeExpression(this)

    val typeElement: CrTypeElement<*>?
        get() = childOfType()
}
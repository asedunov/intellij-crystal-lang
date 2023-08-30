package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIsExpression(node: ASTNode) : CrExpressionImpl(node), CrExpressionWithReceiver {
    override fun accept(visitor: CrVisitor) = visitor.visitIsExpression(this)

    val typeElement: CrTypeElement<*>?
        get() = childOfType()
}
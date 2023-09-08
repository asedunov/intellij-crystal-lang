package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrAsExpression(node: ASTNode) : CrExpressionImpl(node), CrExpressionWithReceiver {
    override fun accept(visitor: CrVisitor) = visitor.visitAsExpression(this)

    val typeElement: CrTypeElement<*>?
        get() = childOfType()
}
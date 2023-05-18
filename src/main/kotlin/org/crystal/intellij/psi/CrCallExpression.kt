package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCallExpression(node: ASTNode) : CrCallLikeExpression(node), CrVisibilityHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCallExpression(this)

    override val ownReceiver: CrExpression?
        get() = childOfType<CrExpression>().takeUnless { it == lastChild }

    override val argumentList: CrArgumentList?
        get() = childOfType()

    override val blockArgument: CrBlockExpression?
        get() = lastChild as? CrBlockExpression
}
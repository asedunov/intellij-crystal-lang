package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrCallExpression(node: ASTNode) : CrCallLikeExpression(node), CrVisibilityHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCallExpression(this)

    override val argumentList: CrArgumentList?
        get() = childOfType()

    override val blockArgument: CrBlockExpression?
        get() = lastChild as? CrBlockExpression
}
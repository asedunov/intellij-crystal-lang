package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCallExpression(node: ASTNode) : CrExpressionImpl(node), CrSimpleNameElementHolder, CrVisibilityHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCallExpression(this)

    val receiver: CrExpression?
        get() = childOfType<CrExpression>().takeUnless { it == lastChild }

    val argumentList: CrArgumentList?
        get() = childOfType()

    val blockArgument: CrBlockExpression?
        get() = lastChild as? CrBlockExpression
}
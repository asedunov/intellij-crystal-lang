package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCallExpression(node: ASTNode) : CrExpressionImpl(node), CrSimpleNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCallExpression(this)

    val receiver: CrExpression?
        get() = firstChild as? CrExpression

    val argumentList: CrArgumentList?
        get() = childOfType()

    val blockArgument: CrBlockExpression?
        get() = childOfType()
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIndexedExpression(node: ASTNode) : CrCallLikeExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIndexedExpression(this)

    override val ownReceiver: CrExpression?
        get() = childOfType()

    override val argumentList: CrArgumentList?
        get() = childOfType()

    override val blockArgument: CrBlockExpression?
        get() = null
}
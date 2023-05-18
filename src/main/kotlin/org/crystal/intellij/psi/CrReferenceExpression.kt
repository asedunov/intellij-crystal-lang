package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_DOT

class CrReferenceExpression(node: ASTNode) : CrCallLikeExpression(node), CrMethodReceiver {
    override fun accept(visitor: CrVisitor) = visitor.visitReferenceExpression(this)

    override val ownReceiver: CrExpression?
        get() = childOfType()

    val hasImplicitReceiver: Boolean
        get() = firstChild.elementType == CR_DOT

    override val argumentList: CrArgumentList?
        get() = null

    override val blockArgument: CrBlockExpression?
        get() = null
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_DOT

class CrReferenceExpression(node: ASTNode) : CrExpressionImpl(node), CrSimpleNameElementHolder, CrMethodReceiver {
    override fun accept(visitor: CrVisitor) = visitor.visitReferenceExpression(this)

    val receiver: CrExpression?
        get() = childOfType()

    val hasImplicitReceiver: Boolean
        get() = firstChild.elementType == CR_DOT
}
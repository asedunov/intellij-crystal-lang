package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrRespondsToExpression(node: ASTNode) : CrExpressionImpl(node), CrExpressionWithReceiver {
    override fun accept(visitor: CrVisitor) = visitor.visitRespondsToExpression(this)

    val symbol: CrSymbolExpression?
        get() {
            val dot = dot
            return if (dot != null) dot.nextSiblingOfType() else childOfType()
        }
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrRespondsToExpression(node: ASTNode) : CrExpressionImpl(node), CrExpressionWithReceiver {
    override fun accept(visitor: CrVisitor) = visitor.visitRespondsToExpression(this)
}
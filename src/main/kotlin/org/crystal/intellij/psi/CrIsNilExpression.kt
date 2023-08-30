package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIsNilExpression(node: ASTNode) : CrExpressionImpl(node), CrExpressionWithReceiver {
    override fun accept(visitor: CrVisitor) = visitor.visitIsNilExpression(this)
}
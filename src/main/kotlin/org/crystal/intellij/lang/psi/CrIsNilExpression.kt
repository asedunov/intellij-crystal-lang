package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrIsNilExpression(node: ASTNode) : CrExpressionImpl(node), CrExpressionWithReceiver {
    override fun accept(visitor: CrVisitor) = visitor.visitIsNilExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrReferenceExpression(node: ASTNode) : CrExpressionImpl(node), CrNameElementHolder, CrMethodReceiver {
    override fun accept(visitor: CrVisitor) = visitor.visitReferenceExpression(this)
}
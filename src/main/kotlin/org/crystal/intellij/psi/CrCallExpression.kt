package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCallExpression(node: ASTNode) : CrExpressionImpl(node), CrSimpleNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCallExpression(this)
}
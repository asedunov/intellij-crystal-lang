package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNamedArgumentExpression(node: ASTNode) : CrExpressionImpl(node), CrNamedElement, CrSimpleNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedArgumentExpression(this)
}
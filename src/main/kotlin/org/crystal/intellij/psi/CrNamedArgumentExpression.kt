package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNamedArgumentExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedArgumentExpression(this)
}
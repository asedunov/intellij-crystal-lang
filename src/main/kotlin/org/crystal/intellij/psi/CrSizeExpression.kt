package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSizeExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSizeExpression(this)
}
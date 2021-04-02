package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrBinaryExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBinaryExpression(this)
}
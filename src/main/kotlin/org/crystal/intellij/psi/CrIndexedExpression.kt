package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIndexedExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIndexedExpression(this)
}
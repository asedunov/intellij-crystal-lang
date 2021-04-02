package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIndexedLHSExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIndexedLHSExpression(this)
}
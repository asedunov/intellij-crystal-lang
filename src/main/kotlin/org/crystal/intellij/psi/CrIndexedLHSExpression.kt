package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIndexedLHSExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIndexedLHSExpression(this)
}
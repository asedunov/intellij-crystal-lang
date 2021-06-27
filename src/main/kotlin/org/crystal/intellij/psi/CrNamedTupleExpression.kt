package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNamedTupleExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedTupleExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrTupleExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTupleExpression(this)
}
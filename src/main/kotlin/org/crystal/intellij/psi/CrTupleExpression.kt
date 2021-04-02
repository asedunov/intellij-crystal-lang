package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrTupleExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTupleExpression(this)
}
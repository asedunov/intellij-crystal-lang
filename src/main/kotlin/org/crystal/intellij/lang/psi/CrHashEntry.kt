package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrHashEntry(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHashEntry(this)

    val leftArgument: CrExpression?
        get() = firstChild as? CrExpression

    val rightArgument: CrExpression?
        get() = leftArgument?.nextSiblingOfType()
}
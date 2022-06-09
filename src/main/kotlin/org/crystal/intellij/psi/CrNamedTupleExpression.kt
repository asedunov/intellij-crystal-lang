package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrNamedTupleExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedTupleExpression(this)

    val constructorType: CrType<*>?
        get() = firstChild as? CrType<*>

    val entries: JBIterable<CrNamedTupleEntry>
        get() = childrenOfType()
}
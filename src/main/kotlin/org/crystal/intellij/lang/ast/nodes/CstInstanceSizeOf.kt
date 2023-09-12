package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstInstanceSizeOf(
    expression: CstNode,
    location: CstLocation? = null
) : CstUnaryExpression(expression, location) {
    fun copy(
        expression: CstNode = this.expression,
        location: CstLocation? = this.location
    ) = CstInstanceSizeOf(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitInstanceSizeOf(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformInstanceSizeOf(this)
}
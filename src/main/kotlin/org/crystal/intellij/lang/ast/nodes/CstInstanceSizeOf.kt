package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstInstanceSizeOf(
    expression: CstNode<*>,
    location: CstLocation? = null
) : CstUnaryExpression<CstInstanceSizeOf>(expression, location) {
    override fun copy(
        expression: CstNode<*>,
        location: CstLocation?
    ) = CstInstanceSizeOf(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitInstanceSizeOf(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformInstanceSizeOf(this)
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstSizeOf(
    expression: CstNode<*>,
    location: CstLocation? = null
) : CstUnaryExpression<CstSizeOf>(expression, location) {
    override fun copy(
        expression: CstNode<*>,
        location: CstLocation?
    ) = CstSizeOf(expression, location)
    
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitSizeOf(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformSizeOf(this)
}
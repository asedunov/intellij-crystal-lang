package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstSplat(
    expression: CstNode<*>,
    location: CstLocation? = null
) : CstUnaryExpression<CstSplat>(expression, location) {
    override fun copy(
        expression: CstNode<*>,
        location: CstLocation?
    ) = CstSplat(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitSplat(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformSplat(this)
}
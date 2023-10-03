package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstDoubleSplat(
    expression: CstNode<*>,
    location: CstLocation? = null
) : CstUnaryExpression<CstDoubleSplat>(expression, location) {
    override fun copy(
        expression: CstNode<*>,
        location: CstLocation?,
    ) = CstDoubleSplat(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitDoubleSplat(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformDoubleSplat(this)
}
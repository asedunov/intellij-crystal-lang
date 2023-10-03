package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstReturn(
    expression: CstNode<*>? = null,
    location: CstLocation? = null
) : CstControlExpression<CstReturn>(expression, location) {
    override fun copy(
        expression: CstNode<*>?,
        location: CstLocation?
    ) = CstReturn(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitReturn(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformReturn(this)
}
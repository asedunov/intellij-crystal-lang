package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstNext(
    expression: CstNode<*>? = null,
    location: CstLocation? = null
) : CstControlExpression<CstNext>(expression, location) {
    override fun copy(
        expression: CstNode<*>?,
        location: CstLocation?
    ) = CstNext(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNext(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformNext(this)
}
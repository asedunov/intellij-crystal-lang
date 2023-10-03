package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstBreak(
    expression: CstNode<*>? = null,
    location: CstLocation? = null
) : CstControlExpression<CstBreak>(expression, location) {
    override fun copy(
        expression: CstNode<*>?,
        location: CstLocation?
    ) = CstBreak(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitBreak(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformBreak(this)
}
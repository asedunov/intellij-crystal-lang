package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstOut(
    expression: CstNode<*>,
    location: CstLocation? = null
) : CstUnaryExpression<CstOut>(expression, location) {
    override fun copy(
        expression: CstNode<*>,
        location: CstLocation?
    ) = CstOut(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitOut(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformOut(this)
}
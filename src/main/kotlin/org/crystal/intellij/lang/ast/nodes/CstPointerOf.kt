package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstPointerOf(
    expression: CstNode<*>,
    location: CstLocation? = null
) : CstUnaryExpression<CstPointerOf>(expression, location) {
    override fun copy(
        expression: CstNode<*>,
        location: CstLocation?
    ) = CstPointerOf(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitPointerOf(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformPointerOf(this)
}
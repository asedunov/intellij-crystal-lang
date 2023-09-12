package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstNot(
    expression: CstNode,
    location: CstLocation? = null
) : CstUnaryExpression(expression, location) {
    fun copy(
        expression: CstNode = this.expression,
        location: CstLocation? = this.location
    ) = CstNot(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNot(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformNot(this)
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstOut(
    expression: CstNode,
    location: CstLocation? = null
) : CstUnaryExpression(expression, location) {
    fun copy(
        expression: CstNode = this.expression,
        location: CstLocation? = this.location
    ) = CstOut(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitOut(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformOut(this)
}
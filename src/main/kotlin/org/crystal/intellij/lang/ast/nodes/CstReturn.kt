package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstReturn(
    expression: CstNode? = null,
    location: CstLocation? = null
) : CstControlExpression(expression, location) {
    fun copy(
        expression: CstNode? = this.expression,
        location: CstLocation? = this.location
    ) = CstReturn(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitReturn(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformReturn(this)
}
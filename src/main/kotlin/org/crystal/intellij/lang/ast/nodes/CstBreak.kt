package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstBreak(
    expression: CstNode? = null,
    location: CstLocation? = null
) : CstControlExpression(expression, location) {
    fun copy(
        expression: CstNode? = this.expression,
        location: CstLocation? = this.location
    ) = CstBreak(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitBreak(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformBreak(this)
}
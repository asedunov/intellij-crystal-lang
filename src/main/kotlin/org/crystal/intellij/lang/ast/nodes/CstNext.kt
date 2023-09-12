package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstNext(
    expression: CstNode? = null,
    location: CstLocation? = null
) : CstControlExpression(expression, location) {
    fun copy(
        expression: CstNode? = this.expression,
        location: CstLocation? = this.location
    ) = CstNext(expression, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNext(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformNext(this)
}
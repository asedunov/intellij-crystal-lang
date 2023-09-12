package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstUntil(
    condition: CstNode,
    body: CstNode = CstNop,
    location: CstLocation? = null
) : CstLoopBase(condition, body, location) {
    fun copy(
        condition: CstNode = this.condition,
        body: CstNode = this.body,
        location: CstLocation? = this.location
    ) = CstUntil(condition, body, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitUntil(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformUntil(this)
}
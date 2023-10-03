package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstUntil(
    condition: CstNode<*>,
    body: CstNode<*> = CstNop,
    location: CstLocation? = null
) : CstLoopBase<CstUntil>(condition, body, location) {
    override fun copy(
        condition: CstNode<*>,
        body: CstNode<*>,
        location: CstLocation?
    ) = CstUntil(condition, body, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitUntil(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformUntil(this)
}
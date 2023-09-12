package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstOr(
    left: CstNode,
    right: CstNode,
    location: CstLocation? = null
) : CstBinaryOp(left, right, location) {
    fun copy(
        left: CstNode = this.left,
        right: CstNode = this.right,
        location: CstLocation? = this.location
    ) = CstOr(left, right, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitOr(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformOr(this)
}
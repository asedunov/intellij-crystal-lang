package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

data object CstNop : CstNode<CstNop>(null), CstSimpleLiteral {
    override fun withLocation(location: CstLocation?) = this

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNop(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformNop(this)
}
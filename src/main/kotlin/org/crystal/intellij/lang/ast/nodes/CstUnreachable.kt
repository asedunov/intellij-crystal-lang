package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

data object CstUnreachable : CstNode<CstUnreachable>(null), CstSimpleLiteral {
    override fun withLocation(location: CstLocation?) = this

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitUnreachable(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformUnreachable(this)
}
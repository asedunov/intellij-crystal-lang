package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

data object CstImplicitObj : CstNode<CstImplicitObj>(null) {
    override fun withLocation(location: CstLocation?) = this

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitImplicitObj(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformImplicitObj(this)
}
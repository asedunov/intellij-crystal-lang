package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

data object CstImplicitObj : CstNode(null) {
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitImplicitObj(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformImplicitObj(this)
}
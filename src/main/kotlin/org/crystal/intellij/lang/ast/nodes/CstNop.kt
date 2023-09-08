package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

data object CstNop : CstNode(null), CstSimpleLiteral {
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNop(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformNop(this)
}
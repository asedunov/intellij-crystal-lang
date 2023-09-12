package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

data class CstProcLiteral(
    val def: CstDef
) : CstNode(def.location) {
    companion object {
        val EMPTY = CstProcLiteral(CstDef("->"))
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitProcLiteral(this)

    override fun acceptChildren(visitor: CstVisitor) {
        def.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformProcLiteral(this)
}
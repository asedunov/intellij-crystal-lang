package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

data class CstProcLiteral(
    val def: CstDef
) : CstNode<CstProcLiteral>(def.location) {
    companion object {
        val EMPTY = CstProcLiteral(CstDef("->"))
    }

    override fun withLocation(location: CstLocation?) = CstProcLiteral(def.withLocation(location))

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitProcLiteral(this)

    override fun acceptChildren(visitor: CstVisitor) {
        def.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformProcLiteral(this)
}
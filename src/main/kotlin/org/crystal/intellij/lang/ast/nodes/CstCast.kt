package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstCast(
    obj: CstNode<*>,
    type: CstNode<*>,
    location: CstLocation? = null
) : CstCastBase<CstCast>(obj, type, location) {
    override fun copy(
        obj: CstNode<*>,
        type: CstNode<*>,
        location: CstLocation?
    ) = CstCast(obj, type, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitCast(this)

    override fun acceptChildren(visitor: CstVisitor) {
        obj.accept(visitor)
        type.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformCast(this)
}
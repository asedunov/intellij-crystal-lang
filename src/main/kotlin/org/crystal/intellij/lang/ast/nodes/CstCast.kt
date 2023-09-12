package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstCast(
    obj: CstNode,
    type: CstNode,
    location: CstLocation? = null
) : CstCastBase(obj, type, location) {
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitCast(this)

    override fun acceptChildren(visitor: CstVisitor) {
        obj.accept(visitor)
        type.accept(visitor)
    }
}
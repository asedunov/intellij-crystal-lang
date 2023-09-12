package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstCast(
    val obj: CstNode,
    val type: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstCast

        if (obj != other.obj) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString() = "Cast(obj=$obj, type=$type)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitCast(this)

    override fun acceptChildren(visitor: CstVisitor) {
        obj.accept(visitor)
        type.accept(visitor)
    }
}
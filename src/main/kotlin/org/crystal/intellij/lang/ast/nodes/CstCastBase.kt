package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

sealed class CstCastBase(
    val obj: CstNode,
    val type: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstCastBase

        if (obj != other.obj) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString() = "$strippedClassName($obj, $type)"

    override fun acceptChildren(visitor: CstVisitor) {
        obj.accept(visitor)
        type.accept(visitor)
    }
}
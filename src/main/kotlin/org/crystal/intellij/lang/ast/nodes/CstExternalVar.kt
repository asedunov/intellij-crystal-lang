package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstExternalVar(
    val name: String,
    val type: CstNode,
    val realName: String? = null,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstExternalVar

        if (name != other.name) return false
        if (type != other.type) return false
        if (realName != other.realName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (realName?.hashCode() ?: 0)
        return result
    }

    override fun toString() = buildString {
        append("ExternalVar(")
        append(name)
        append(", type=$type")
        if (realName != null) append(", realName=$realName")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitExternalVar(this)

    override fun acceptChildren(visitor: CstVisitor) {
        type.accept(visitor)
    }
}
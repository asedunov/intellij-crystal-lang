package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstLibDef(
    val name: CstPath,
    val body: CstNode = CstNop,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstLibDef

        if (name != other.name) return false
        if (body != other.body) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + body.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("LibDef(")
        append(name)
        if (body != CstNop) append(", body=$body")
        append(")")
    }
}
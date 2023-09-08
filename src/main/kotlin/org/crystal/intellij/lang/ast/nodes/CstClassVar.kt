package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstClassVar(
    val name: String,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstClassVar

        return name == other.name
    }

    override fun hashCode() = name.hashCode()

    override fun toString() = "ClassVar($name)"
}

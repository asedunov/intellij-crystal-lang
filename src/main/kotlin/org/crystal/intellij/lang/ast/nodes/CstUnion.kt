package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstUnion(
    val types: List<CstNode>,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstUnion

        return types == other.types
    }

    override fun hashCode() = types.hashCode()

    override fun toString() = "Union($types)"
}
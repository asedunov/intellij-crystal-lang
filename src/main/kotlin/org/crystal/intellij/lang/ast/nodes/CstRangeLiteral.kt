package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstRangeLiteral(
    val from: CstNode,
    val to: CstNode,
    val isExclusive: Boolean,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstRangeLiteral

        if (from != other.from) return false
        if (to != other.to) return false
        if (isExclusive != other.isExclusive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        result = 31 * result + isExclusive.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("RangeLiteral(")
        append("from=$from, to=$to")
        if (isExclusive) append(", isExclusive")
        append(")")
    }
}
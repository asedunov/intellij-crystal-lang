package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstNamedTupleLiteral(
    val entries: List<Entry>,
    location: CstLocation? = null
) : CstNode(location) {
    data class Entry(
        val key: String,
        val value: CstNode
    ) {
        override fun toString() = "Entry($key, $value)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstNamedTupleLiteral

        return entries == other.entries
    }

    override fun hashCode() = entries.hashCode()

    override fun toString() = "NamedTupleLiteral($entries)"
}
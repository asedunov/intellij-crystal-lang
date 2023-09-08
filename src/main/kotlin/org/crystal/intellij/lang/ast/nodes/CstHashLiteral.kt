package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstHashLiteral(
    val entries: List<Entry> = emptyList(),
    val elementType: Entry? = null,
    val receiverType: CstNode? = null,
    location: CstLocation? = null
) : CstNode(location) {
    data class Entry(
        val key: CstNode,
        val value: CstNode
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstHashLiteral

        if (entries != other.entries) return false
        if (elementType != other.elementType) return false
        if (receiverType != other.receiverType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = entries.hashCode()
        result = 31 * result + (elementType?.hashCode() ?: 0)
        result = 31 * result + (receiverType?.hashCode() ?: 0)
        return result
    }

    override fun toString() = sequence {
        if (entries.isNotEmpty()) yield("entries=$entries")
        if (elementType != null) yield("elementType=$elementType")
        if (receiverType != null) yield("receiverType=$receiverType")
    }.joinToString(prefix = "HashLiteral(", postfix = ")")
}
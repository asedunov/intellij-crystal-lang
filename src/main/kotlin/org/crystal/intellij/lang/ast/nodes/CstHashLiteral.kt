package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstHashLiteral(
    val entries: List<Entry> = emptyList(),
    val elementType: Entry? = null,
    val receiverType: CstNode? = null,
    location: CstLocation? = null
) : CstNode(location) {
    data class Entry(
        val key: CstNode,
        val value: CstNode
    ) {
        fun accept(visitor: CstVisitor) {
            key.accept(visitor)
            value.accept(visitor)
        }
    }

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

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitHashLiteral(this)

    override fun acceptChildren(visitor: CstVisitor) {
        receiverType?.accept(visitor)
        entries.forEach { it.accept(visitor) }
        elementType?.accept(visitor)
    }
}
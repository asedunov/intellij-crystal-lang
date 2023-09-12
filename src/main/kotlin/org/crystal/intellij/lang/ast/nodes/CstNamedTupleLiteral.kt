package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstNamedTupleLiteral(
    val entries: List<Entry>,
    location: CstLocation? = null
) : CstNode(location) {
    data class Entry(
        val key: String,
        val value: CstNode
    ) {
        override fun toString() = "Entry($key, $value)"

        fun accept(visitor: CstVisitor) {
            value.accept(visitor)
        }
    }

    fun copy(
        entries: List<Entry> = this.entries,
        location: CstLocation? = this.location
    ) = CstNamedTupleLiteral(entries, location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstNamedTupleLiteral

        return entries == other.entries
    }

    override fun hashCode() = entries.hashCode()

    override fun toString() = "NamedTupleLiteral($entries)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNamedTupleLiteral(this)

    override fun acceptChildren(visitor: CstVisitor) {
        entries.forEach { it.accept(visitor) }
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformNamedTupleLiteral(this)
}
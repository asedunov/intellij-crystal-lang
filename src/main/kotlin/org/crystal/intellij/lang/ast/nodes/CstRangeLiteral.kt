package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstRangeLiteral(
    val from: CstNode<*>,
    val to: CstNode<*>,
    val isExclusive: Boolean,
    location: CstLocation? = null
) : CstNode<CstRangeLiteral>(location) {
    fun copy(
        from: CstNode<*> = this.from,
        to: CstNode<*> = this.to,
        isExclusive: Boolean = this.isExclusive,
        location: CstLocation? = this.location
    ) = CstRangeLiteral(from, to, isExclusive, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

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

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitRangeLiteral(this)

    override fun acceptChildren(visitor: CstVisitor) {
        from.accept(visitor)
        to.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformRangeLiteral(this)
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstRescue(
    val body: CstNode<*> = CstNop,
    val types: List<CstNode<*>> = emptyList(),
    val name: String? = null,
    location: CstLocation? = null
) : CstNode<CstRescue>(location) {
    companion object {
        val EMPTY = CstRescue()
    }

    fun copy(
        body: CstNode<*> = this.body,
        types: List<CstNode<*>> = this.types,
        name: String? = this.name,
        location: CstLocation? = this.location
    ) = CstRescue(body, types, name, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstRescue

        if (body != other.body) return false
        if (types != other.types) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = body.hashCode()
        result = 31 * result + types.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }

    override fun toString() = sequence {
        if (body != CstNop) yield("body=$body")
        if (types.isNotEmpty()) yield("types=$types")
        if (name != null) yield("name=$name")
    }.joinToString(prefix = "Rescue(", postfix = ")")

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitRescue(this)

    override fun acceptChildren(visitor: CstVisitor) {
        body.accept(visitor)
        types.forEach { it.accept(visitor) }
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformRescue(this)
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstOffsetOf(
    val type: CstNode,
    val offset: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    fun copy(
        type: CstNode = this.type,
        offset: CstNode = this.offset,
        location: CstLocation? = this.location
    ) = CstOffsetOf(type, offset, location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstOffsetOf

        if (type != other.type) return false
        if (offset != other.offset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + offset.hashCode()
        return result
    }

    override fun toString() = "OffsetOf(type=$type, offset=$offset)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitOffsetOf(this)

    override fun acceptChildren(visitor: CstVisitor) {
        type.accept(visitor)
        offset.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformOffsetOf(this)
}
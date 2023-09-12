package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstCStructOrUnionDef(
    val name: String,
    val body: CstNode = CstNop,
    val isUnion: Boolean = false,
    location: CstLocation? = null
) : CstNode(location) {
    fun copy(
        name: String = this.name,
        body: CstNode = this.body,
        isUnion: Boolean = this.isUnion,
        location: CstLocation? = this.location
    ) = CstCStructOrUnionDef(name, body, isUnion, location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstCStructOrUnionDef

        if (name != other.name) return false
        if (body != other.body) return false
        if (isUnion != other.isUnion) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + isUnion.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("CStructOrUnionDef(")
        append(name)
        if (body != CstNop) append(", body=$body")
        if (isUnion) append(", isUnion")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitCStructOrUnionDef(this)

    override fun acceptChildren(visitor: CstVisitor) {
        body.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformCStructOrUnionDef(this)
}
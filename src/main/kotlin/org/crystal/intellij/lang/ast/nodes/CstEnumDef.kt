package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstEnumDef(
    val name: CstPath,
    val members: List<CstNode<*>> = emptyList(),
    val baseType: CstNode<*>? = null,
    location: CstLocation? = null
) : CstNode<CstEnumDef>(location) {
    fun copy(
        name: CstPath = this.name,
        members: List<CstNode<*>> = this.members,
        baseType: CstNode<*>? = this.baseType,
        location: CstLocation? = this.location
    ) = CstEnumDef(name, members, baseType, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstEnumDef

        if (name != other.name) return false
        if (members != other.members) return false
        if (baseType != other.baseType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + members.hashCode()
        result = 31 * result + (baseType?.hashCode() ?: 0)
        return result
    }

    override fun toString() = buildString {
        append("EnumDef(")
        append(name)
        if (members.isNotEmpty()) append(", members=$members")
        if (baseType != null) append(", baseType=$baseType")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitEnumDef(this)

    override fun acceptChildren(visitor: CstVisitor) {
        members.forEach { it.accept(visitor) }
        baseType?.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformEnumDef(this)
}
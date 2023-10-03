package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstArg(
    val name: String,
    val defaultValue: CstNode<*>? = null,
    val restriction: CstNode<*>? = null,
    val externalName: String = name,
    val annotations: List<CstAnnotation> = emptyList(),
    location: CstLocation? = null
) : CstNode<CstArg>(location) {
    fun copy(
        name: String = this.name,
        defaultValue: CstNode<*>? = this.defaultValue,
        restriction: CstNode<*>? = this.restriction,
        externalName: String = this.externalName,
        annotations: List<CstAnnotation> = this.annotations,
        location: CstLocation? = this.location
    ) = CstArg(name, defaultValue, restriction, externalName, annotations, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstArg

        if (name != other.name) return false
        if (defaultValue != other.defaultValue) return false
        if (restriction != other.restriction) return false
        if (externalName != other.externalName) return false
        if (annotations != other.annotations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (defaultValue?.hashCode() ?: 0)
        result = 31 * result + (restriction?.hashCode() ?: 0)
        result = 31 * result + externalName.hashCode()
        result = 31 * result + annotations.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("Arg(")
        append(name)
        if (defaultValue != null) append(", defaultValue=$defaultValue")
        if (restriction != null) append(", restriction=$restriction")
        if (externalName != name) append(", externalName=$externalName")
        if (annotations.isNotEmpty()) append(", annotations=$annotations")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitArg(this)

    override fun acceptChildren(visitor: CstVisitor) {
        defaultValue?.accept(visitor)
        restriction?.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformArg(this)
}
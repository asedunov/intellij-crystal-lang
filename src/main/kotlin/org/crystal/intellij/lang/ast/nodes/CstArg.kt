package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstArg(
    val name: String,
    val defaultValue: CstNode? = null,
    val restriction: CstNode? = null,
    val externalName: String? = null,
    val annotations: List<CstAnnotation> = emptyList(),
    location: CstLocation? = null
) : CstNode(location) {
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
        result = 31 * result + (externalName?.hashCode() ?: 0)
        result = 31 * result + annotations.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("Arg(")
        append(name)
        if (defaultValue != null) append(", defaultValue=$defaultValue")
        if (restriction != null) append(", restriction=$restriction")
        if (externalName != null) append(", externalName=$externalName")
        if (annotations.isNotEmpty()) append(", annotations=$annotations")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitArg(this)

    override fun acceptChildren(visitor: CstVisitor) {
        defaultValue?.accept(visitor)
        restriction?.accept(visitor)
    }
}
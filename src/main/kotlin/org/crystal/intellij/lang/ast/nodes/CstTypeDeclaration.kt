package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstTypeDeclaration(
    val variable: CstNode,
    val type: CstNode,
    val value: CstNode? = null,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstTypeDeclaration

        if (variable != other.variable) return false
        if (type != other.type) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = variable.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }

    override fun toString() = buildString {
        append("TypeDeclaration(")
        append(variable)
        append(", type=$type")
        if (value != null) append(", value=$value")
        append(")")
    }
}
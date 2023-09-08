package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstArrayLiteral(
    val elements: List<CstNode> = emptyList(),
    val elementType: CstNode? = null,
    val receiverType: CstNode? = null,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstArrayLiteral

        if (elements != other.elements) return false
        if (elementType != other.elementType) return false
        if (receiverType != other.receiverType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = elements.hashCode()
        result = 31 * result + (elementType?.hashCode() ?: 0)
        result = 31 * result + (receiverType?.hashCode() ?: 0)
        return result
    }

    override fun toString() = sequence {
        if (elements.isNotEmpty()) yield("elements=$elements")
        if (elementType != null) yield("elementType=$elementType")
        if (receiverType != null) yield("receiverType=$receiverType")
    }.joinToString(prefix = "ArrayLiteral(", postfix = ")")
}
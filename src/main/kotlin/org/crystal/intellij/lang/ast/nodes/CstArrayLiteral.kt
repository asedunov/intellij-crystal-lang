package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstArrayLiteral(
    val elements: List<CstNode<*>> = emptyList(),
    val elementType: CstNode<*>? = null,
    val receiverType: CstNode<*>? = null,
    location: CstLocation? = null
) : CstNode<CstArrayLiteral>(location) {
    fun copy(
        elements: List<CstNode<*>> = this.elements,
        elementType: CstNode<*>? = this.elementType,
        receiverType: CstNode<*>? = this.receiverType,
        location: CstLocation? = this.location
    ) = CstArrayLiteral(elements, elementType, receiverType, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

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

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitArrayLiteral(this)

    override fun acceptChildren(visitor: CstVisitor) {
        receiverType?.accept(visitor)
        elements.forEach { it.accept(visitor) }
        elementType?.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformArrayLiteral(this)
}
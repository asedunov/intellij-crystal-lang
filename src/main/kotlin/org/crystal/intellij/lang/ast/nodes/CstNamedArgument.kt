package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstNamedArgument(
    val name: String,
    val value: CstNode<*>,
    location: CstLocation? = null
) : CstNode<CstNamedArgument>(location) {
    fun copy(
        name: String = this.name,
        value: CstNode<*> = this.value,
        location: CstLocation? = this.location
    ) = CstNamedArgument(name, value, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstNamedArgument

        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString() = "NamedArgument($name, $value)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNamedArgument(this)

    override fun acceptChildren(visitor: CstVisitor) {
        value.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformNamedArgument(this)
}
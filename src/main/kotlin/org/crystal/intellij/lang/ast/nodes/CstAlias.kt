package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstAlias(
    val name: CstPath,
    val value: CstNode<*>,
    location: CstLocation? = null
) : CstNode<CstAlias>(location) {
    fun copy(
        name: CstPath = this.name,
        value: CstNode<*> = this.value,
        location: CstLocation? = this.location
    ) = CstAlias(name, value, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun toString() = "Alias($name, value=$value)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstAlias

        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitAlias(this)

    override fun acceptChildren(visitor: CstVisitor) {
        value.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformAlias(this)
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstInclude(
    val name: CstNode<*>,
    location: CstLocation? = null
) : CstNode<CstInclude>(location) {
    fun copy(
        name: CstNode<*> = this.name,
        location: CstLocation? = this.location
    ) = CstInclude(name, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstInclude

        return name == other.name
    }

    override fun hashCode() = name.hashCode()

    override fun toString() = "Include($name)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitInclude(this)

    override fun acceptChildren(visitor: CstVisitor) {
        name.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformInclude(this)
}
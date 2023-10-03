package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstExtend(
    val name: CstNode<*>,
    location: CstLocation? = null
) : CstNode<CstExtend>(location) {
    fun copy(
        name: CstNode<*> = this.name,
        location: CstLocation? = this.location
    ) = CstExtend(name, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstExtend

        return name == other.name
    }

    override fun hashCode() = name.hashCode()

    override fun toString() = "Extend($name)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitExtend(this)

    override fun acceptChildren(visitor: CstVisitor) {
        name.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformExtend(this)
}

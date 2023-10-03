package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstGlobal(
    val name: String,
    location: CstLocation? = null
) : CstNode<CstGlobal>(location) {
    fun copy(
        name: String = this.name,
        location: CstLocation? = this.location
    ) = CstGlobal(name, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstGlobal

        return name == other.name
    }

    override fun hashCode() = name.hashCode()

    override fun toString() = "Global($name)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitGlobal(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformGlobal(this)
}

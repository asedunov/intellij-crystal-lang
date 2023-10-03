package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstInstanceVar(
    val name: String,
    location: CstLocation? = null
) : CstNode<CstInstanceVar>(location) {
    fun copy(
        name: String = this.name,
        location: CstLocation? = this.location
    ) = CstInstanceVar(name, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstInstanceVar

        return name == other.name
    }

    override fun hashCode() = name.hashCode()

    override fun toString() = "InstanceVar($name)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitInstanceVar(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformInstanceVar(this)
}

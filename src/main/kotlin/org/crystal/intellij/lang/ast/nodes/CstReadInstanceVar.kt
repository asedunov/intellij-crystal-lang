package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstReadInstanceVar(
    val receiver: CstNode<*>,
    val name: String,
    location: CstLocation? = null
) : CstNode<CstReadInstanceVar>(location) {
    fun copy(
        receiver: CstNode<*> = this.receiver,
        name: String = this.name,
        location: CstLocation? = this.location
    ) = CstReadInstanceVar(receiver, name, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstReadInstanceVar

        if (receiver != other.receiver) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = receiver.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString() = "CstReadInstanceVar(receiver=$receiver, name=$name)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitReadInstanceVar(this)

    override fun acceptChildren(visitor: CstVisitor) {
        receiver.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformReadInstanceVar(this)
}
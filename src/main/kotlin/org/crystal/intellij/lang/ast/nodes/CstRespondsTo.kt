package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstRespondsTo(
    override val obj: CstNode<*>,
    val name: String,
    location: CstLocation? = null
) : CstNodeWithReceiver<CstRespondsTo>(location) {
    fun copy(
        obj: CstNode<*> = this.obj,
        name: String = this.name,
        location: CstLocation? = this.location
    ) = CstRespondsTo(obj, name, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun withReceiver(obj: CstNode<*>?) = copy(obj = obj ?: CstNop)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstRespondsTo

        if (obj != other.obj) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString() = "RespondsTo(receiver=$obj, name=$name)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitRespondsTo(this)

    override fun acceptChildren(visitor: CstVisitor) {
        obj.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformRespondsTo(this)
}
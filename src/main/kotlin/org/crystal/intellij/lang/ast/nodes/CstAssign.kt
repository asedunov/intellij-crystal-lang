package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstAssign(
    val target: CstNode<*>,
    val value: CstNode<*>,
    location: CstLocation? = null
) : CstNode<CstAssign>(location) {
    fun copy(
        target: CstNode<*> = this.target,
        value: CstNode<*> = this.value,
        location: CstLocation? = this.location
    ) = CstAssign(target, value, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstAssign

        if (target != other.target) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = target.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString() = "Assign($target, $value)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitAssign(this)

    override fun acceptChildren(visitor: CstVisitor) {
        target.accept(visitor)
        value.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformAssign(this)
}

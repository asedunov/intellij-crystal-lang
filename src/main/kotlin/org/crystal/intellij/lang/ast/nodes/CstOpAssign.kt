package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstOpAssign(
    val target: CstNode,
    val op: String,
    val value: CstNode,
    location: CstLocation? = null,
    override val nameLocation: CstLocation? = null
) : CstNode(location) {
    fun copy(
        target: CstNode = this.target,
        op: String = this.op,
        value: CstNode = this.value,
        location: CstLocation? = this.location,
        nameLocation: CstLocation? = this.nameLocation,
    ) = CstOpAssign(target, op, value, location, nameLocation)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstOpAssign

        if (target != other.target) return false
        if (op != other.op) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = target.hashCode()
        result = 31 * result + op.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString() = "OpAssign(target=$target, op=$op, value=$value)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitOpAssign(this)

    override fun acceptChildren(visitor: CstVisitor) {
        target.accept(visitor)
        value.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformOpAssign(this)
}
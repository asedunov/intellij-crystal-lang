package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstOpAssign(
    val target: CstNode,
    val op: String,
    val value: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
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
}
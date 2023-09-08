package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstAssign(
    val target: CstNode,
    val value: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
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
}

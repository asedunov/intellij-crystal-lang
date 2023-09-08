package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstUntil(
    val condition: CstNode,
    val body: CstNode = CstNop,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstUntil

        if (condition != other.condition) return false
        if (body != other.body) return false

        return true
    }

    override fun hashCode(): Int {
        var result = condition.hashCode()
        result = 31 * result + body.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("While(")
        append(condition)
        if (body != CstNop) append(", body=$body")
        append(")")
    }
}
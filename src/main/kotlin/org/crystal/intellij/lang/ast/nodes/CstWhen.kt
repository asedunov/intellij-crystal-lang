package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstWhen(
    val conditions: List<CstNode>,
    val body: CstNode,
    val isExhaustive: Boolean = false,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstWhen

        if (conditions != other.conditions) return false
        if (body != other.body) return false
        if (isExhaustive != other.isExhaustive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = conditions.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + isExhaustive.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("When(")
        append("conditions=$conditions")
        append(", body=$body")
        if (isExhaustive) append(", isExhaustive")
        append(")")
    }
}
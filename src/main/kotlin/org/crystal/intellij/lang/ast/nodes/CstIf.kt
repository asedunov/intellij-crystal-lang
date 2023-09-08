package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstIf(
    val condition: CstNode,
    val thenBranch: CstNode = CstNop,
    val elseBranch: CstNode = CstNop,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstIf

        if (condition != other.condition) return false
        if (thenBranch != other.thenBranch) return false
        if (elseBranch != other.elseBranch) return false

        return true
    }

    override fun hashCode(): Int {
        var result = condition.hashCode()
        result = 31 * result + thenBranch.hashCode()
        result = 31 * result + elseBranch.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("If(")
        append(condition)
        if (thenBranch != CstNop) append(", then=$thenBranch")
        if (elseBranch != CstNop) append(", else=$elseBranch")
        append(")")
    }
}
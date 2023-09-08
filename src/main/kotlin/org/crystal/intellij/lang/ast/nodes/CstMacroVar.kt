package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstMacroVar(
    val name: String,
    val exps: List<CstNode> = emptyList(),
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstMacroVar

        if (name != other.name) return false
        if (exps != other.exps) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + exps.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("MacroVar(")
        append(name)
        if (exps.isNotEmpty()) append(", exps=$exps")
        append(")")
    }
}
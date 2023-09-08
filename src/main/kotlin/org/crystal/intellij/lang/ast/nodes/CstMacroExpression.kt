package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstMacroExpression(
    val exp: CstNode,
    val isOutput: Boolean = true,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstMacroExpression

        if (exp != other.exp) return false
        if (isOutput != other.isOutput) return false

        return true
    }

    override fun hashCode(): Int {
        var result = exp.hashCode()
        result = 31 * result + isOutput.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("MacroExpression(")
        append("exp=$exp")
        if (isOutput) append(", isOutput")
        append(")")
    }
}
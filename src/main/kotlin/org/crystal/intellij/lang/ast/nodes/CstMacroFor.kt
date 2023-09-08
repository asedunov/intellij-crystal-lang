package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstMacroFor(
    val vars: List<CstVar>,
    val exp: CstNode,
    val body: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstMacroFor

        if (vars != other.vars) return false
        if (exp != other.exp) return false
        if (body != other.body) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vars.hashCode()
        result = 31 * result + exp.hashCode()
        result = 31 * result + body.hashCode()
        return result
    }

    override fun toString() = "MacroFor(vars=$vars, exp=$exp, body=$body)"
}
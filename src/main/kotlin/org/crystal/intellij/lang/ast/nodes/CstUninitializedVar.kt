package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstUninitializedVar(
    val variable: CstNode,
    val declaredType: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstUninitializedVar

        if (variable != other.variable) return false
        if (declaredType != other.declaredType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = variable.hashCode()
        result = 31 * result + declaredType.hashCode()
        return result
    }

    override fun toString() = "UninitializedVar(variable=$variable, declaredType=$declaredType)"
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstVar(
    val name: String,
    location: CstLocation? = null
) : CstNode(location) {
    companion object {
        val EMPTY = CstVar("")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstVar

        return name == other.name
    }
    override fun hashCode() = name.hashCode()

    override fun toString() = "Var($name)"
}
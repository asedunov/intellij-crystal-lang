package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstMacroLiteral(
    val value: String,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstMacroLiteral

        return value == other.value
    }

    override fun hashCode() = value.hashCode()

    override fun toString() = "MacroLiteral($value)"
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstSymbolLiteral(
    val value: String,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstSymbolLiteral

        return value == other.value
    }

    override fun hashCode() = value.hashCode()

    override fun toString() = "SymbolLiteral($value)"
}
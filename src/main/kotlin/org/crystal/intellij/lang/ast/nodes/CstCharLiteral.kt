package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstCharLiteral(
    val value: Int,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstCharLiteral

        return value == other.value
    }

    override fun hashCode() = value

    override fun toString() = buildString {
        append("CharLiteral(").appendCodePoint(value).append(")")
    }
}
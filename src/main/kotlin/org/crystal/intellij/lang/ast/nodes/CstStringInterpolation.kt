package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstStringInterpolation(
    val expressions: List<CstNode>,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstStringInterpolation

        return expressions == other.expressions
    }

    override fun hashCode() = expressions.hashCode()

    override fun toString() = "StringInterpolation($expressions)"
}
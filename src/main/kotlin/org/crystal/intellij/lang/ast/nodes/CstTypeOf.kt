package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstTypeOf(
    val expressions: List<CstNode>,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstTypeOf

        return expressions == other.expressions
    }

    override fun hashCode() = expressions.hashCode()

    override fun toString() = "TypeOf($expressions)"
}
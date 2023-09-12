package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

sealed class CstControlExpression(
    val expression: CstNode? = null,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstControlExpression

        return expression == other.expression
    }

    override fun hashCode() = expression.hashCode()

    override fun toString() = buildString {
        append(strippedClassName)
        if (expression != null) append("($expression)")
    }

    override fun acceptChildren(visitor: CstVisitor) {
        expression?.accept(visitor)
    }
}
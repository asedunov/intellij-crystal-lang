package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

sealed class CstControlExpression<T : CstControlExpression<T>>(
    val expression: CstNode<*>? = null,
    location: CstLocation? = null
) : CstNode<T>(location) {
    abstract fun copy(
        expression: CstNode<*>? = this.expression,
        location: CstLocation? = this.location
    ): T

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstControlExpression<*>

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
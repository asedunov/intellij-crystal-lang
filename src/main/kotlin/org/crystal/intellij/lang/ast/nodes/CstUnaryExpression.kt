package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

sealed class CstUnaryExpression(
    val expression: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstUnaryExpression

        return expression == other.expression
    }

    override fun hashCode() = expression.hashCode()

    override fun toString() = "$strippedClassName($expression)"

    override fun acceptChildren(visitor: CstVisitor) {
        expression.accept(visitor)
    }
}
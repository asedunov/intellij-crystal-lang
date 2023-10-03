package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

sealed class CstLoopBase<T : CstLoopBase<T>>(
    val condition: CstNode<*>,
    val body: CstNode<*> = CstNop,
    location: CstLocation? = null
) : CstNode<T>(location) {
    abstract fun copy(
        condition: CstNode<*> = this.condition,
        body: CstNode<*> = this.body,
        location: CstLocation? = this.location
    ): T

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstLoopBase<*>

        if (condition != other.condition) return false
        if (body != other.body) return false

        return true
    }

    override fun hashCode(): Int {
        var result = condition.hashCode()
        result = 31 * result + body.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("$strippedClassName(")
        append(condition)
        if (body != CstNop) append(", body=$body")
        append(")")
    }

    override fun acceptChildren(visitor: CstVisitor) {
        condition.accept(visitor)
        body.accept(visitor)
    }
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

sealed class CstConditionalExpression<T : CstConditionalExpression<T>>(
    val condition: CstNode<*>,
    val thenBranch: CstNode<*> = CstNop,
    val elseBranch: CstNode<*> = CstNop,
    location: CstLocation? = null
) : CstNode<T>(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstConditionalExpression<*>

        if (condition != other.condition) return false
        if (thenBranch != other.thenBranch) return false
        if (elseBranch != other.elseBranch) return false

        return true
    }

    override fun hashCode(): Int {
        var result = condition.hashCode()
        result = 31 * result + thenBranch.hashCode()
        result = 31 * result + elseBranch.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("$strippedClassName(")
        append(condition)
        if (thenBranch != CstNop) append(", then=$thenBranch")
        if (elseBranch != CstNop) append(", else=$elseBranch")
        append(")")
    }

    override fun acceptChildren(visitor: CstVisitor) {
        condition.accept(visitor)
        thenBranch.accept(visitor)
        elseBranch.accept(visitor)
    }
}
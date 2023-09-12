package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstCase(
    val condition: CstNode?,
    val whenBranches: List<CstWhen>,
    val elseBranch: CstNode?,
    val isExhaustive: Boolean,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstCase

        if (condition != other.condition) return false
        if (whenBranches != other.whenBranches) return false
        if (elseBranch != other.elseBranch) return false
        if (isExhaustive != other.isExhaustive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = condition?.hashCode() ?: 0
        result = 31 * result + whenBranches.hashCode()
        result = 31 * result + (elseBranch?.hashCode() ?: 0)
        result = 31 * result + isExhaustive.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("Case(")
        append("condition=$condition")
        if (whenBranches.isNotEmpty()) append(", whens=$whenBranches")
        if (elseBranch != null) append(", else=$elseBranch")
        if (isExhaustive) append(", isExhaustive")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitCase(this)

    override fun acceptChildren(visitor: CstVisitor) {
        condition?.accept(visitor)
        whenBranches.forEach { it.accept(visitor) }
        elseBranch?.accept(visitor)
    }
}
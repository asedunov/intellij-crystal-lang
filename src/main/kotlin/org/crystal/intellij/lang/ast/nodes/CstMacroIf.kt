package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstMacroIf(
    val condition: CstNode<*>,
    val thenBranch: CstNode<*> = CstNop,
    val elseBranch: CstNode<*> = CstNop,
    location: CstLocation? = null
) : CstNode<CstMacroIf>(location) {
    fun copy(
        condition: CstNode<*> = this.condition,
        thenBranch: CstNode<*> = this.thenBranch,
        elseBranch: CstNode<*> = this.elseBranch,
        location: CstLocation? = this.location
    ) = CstMacroIf(condition, thenBranch, elseBranch, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstMacroIf

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
        append("MacroIf(")
        append(condition)
        if (thenBranch != CstNop) append(", thenBranch=$thenBranch")
        if (elseBranch != CstNop) append(", elseBranch=$elseBranch")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitMacroIf(this)

    override fun acceptChildren(visitor: CstVisitor) {
        condition.accept(visitor)
        thenBranch.accept(visitor)
        elseBranch.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformMacroIf(this)
}
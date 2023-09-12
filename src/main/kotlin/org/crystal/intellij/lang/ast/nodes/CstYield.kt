package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstYield(
    val expressions: List<CstNode> = emptyList(),
    val scope: CstNode? = null,
    val hasParentheses: Boolean = false,
    location: CstLocation? = null
) : CstNode(location) {
    companion object {
        val EMPTY = CstYield()
    }

    fun copy(
        expressions: List<CstNode> = this.expressions,
        scope: CstNode? = this.scope,
        hasParentheses: Boolean = this.hasParentheses,
        location: CstLocation? = this.location
    ) = CstYield(expressions, scope, hasParentheses, location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstYield

        if (expressions != other.expressions) return false
        if (scope != other.scope) return false
        if (hasParentheses != other.hasParentheses) return false

        return true
    }

    override fun hashCode(): Int {
        var result = expressions.hashCode()
        result = 31 * result + (scope?.hashCode() ?: 0)
        result = 31 * result + hasParentheses.hashCode()
        return result
    }

    override fun toString() = sequence {
        if (expressions.isNotEmpty()) yield("expressions=$expressions")
        if (scope != null) yield("scope=$scope")
        if (hasParentheses) yield("hasParentheses")
    }.joinToString(prefix = "Yield(", postfix = ")")

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitYield(this)

    override fun acceptChildren(visitor: CstVisitor) {
        scope?.accept(visitor)
        expressions.forEach { it.accept(visitor) }
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformYield(this)
}
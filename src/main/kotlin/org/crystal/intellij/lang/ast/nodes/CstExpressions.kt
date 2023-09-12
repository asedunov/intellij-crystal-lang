package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.lexer.CrystalTokenType

class CstExpressions(
    val expressions: List<CstNode> = emptyList(),
    val keyword: CrystalTokenType? = null,
    location: CstLocation? = null
) : CstNode(location) {
    companion object {
        val EMPTY = CstExpressions()

        fun from(nodes: List<CstNode>, location: CstLocation? = null): CstNode {
            if (nodes.isEmpty()) return CstNop
            val node = nodes.singleOrNull()
            if (node != null) return node
            return CstExpressions(nodes, location = location)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstExpressions

        return expressions == other.expressions
    }

    override fun hashCode() = expressions.hashCode()

    override fun toString() = "Expressions($expressions)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitExpressions(this)

    override fun acceptChildren(visitor: CstVisitor) {
        expressions.forEach { it.accept(visitor) }
    }
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstWhile(
    val condition: CstNode,
    val body: CstNode = CstNop,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstWhile

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
        append("While(")
        append(condition)
        if (body != CstNop) append(", body=$body")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitWhile(this)

    override fun acceptChildren(visitor: CstVisitor) {
        condition.accept(visitor)
        body.accept(visitor)
    }
}
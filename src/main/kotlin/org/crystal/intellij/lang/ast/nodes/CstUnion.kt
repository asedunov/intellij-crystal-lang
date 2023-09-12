package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstUnion(
    val types: List<CstNode>,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstUnion

        return types == other.types
    }

    override fun hashCode() = types.hashCode()

    override fun toString() = "Union($types)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitUnion(this)

    override fun acceptChildren(visitor: CstVisitor) {
        types.forEach { it.accept(visitor) }
    }
}
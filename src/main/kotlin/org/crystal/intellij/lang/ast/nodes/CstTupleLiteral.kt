package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstTupleLiteral(
    val elements: List<CstNode>,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstTupleLiteral

        return elements == other.elements
    }

    override fun hashCode() = elements.hashCode()

    override fun toString() = "TupleLiteral($elements)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitTupleLiteral(this)

    override fun acceptChildren(visitor: CstVisitor) {
        elements.forEach { it.accept(visitor) }
    }
}
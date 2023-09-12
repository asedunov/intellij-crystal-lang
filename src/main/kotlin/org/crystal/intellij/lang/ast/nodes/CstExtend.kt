package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstExtend(
    val name: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstExtend

        return name == other.name
    }

    override fun hashCode() = name.hashCode()

    override fun toString() = "Extend($name)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitExtend(this)

    override fun acceptChildren(visitor: CstVisitor) {
        name.accept(visitor)
    }
}

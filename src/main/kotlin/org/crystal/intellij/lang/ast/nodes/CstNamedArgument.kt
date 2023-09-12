package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstNamedArgument(
    val name: String,
    val value: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstNamedArgument

        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString() = "NamedArgument($name, $value)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNamedArgument(this)

    override fun acceptChildren(visitor: CstVisitor) {
        value.accept(visitor)
    }
}
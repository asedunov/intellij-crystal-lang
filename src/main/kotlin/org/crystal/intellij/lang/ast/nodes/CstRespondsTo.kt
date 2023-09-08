package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstRespondsTo(
    val receiver: CstNode,
    val name: String,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstRespondsTo

        if (receiver != other.receiver) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = receiver.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString() = "RespondsTo(receiver=$receiver, name=$name)"
}
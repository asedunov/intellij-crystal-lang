package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstTypeDef(
    val name: String,
    val typeSpec: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstTypeDef

        if (name != other.name) return false
        if (typeSpec != other.typeSpec) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + typeSpec.hashCode()
        return result
    }

    override fun toString() = "TypeSpec($name, typeSpec=$typeSpec)"
}
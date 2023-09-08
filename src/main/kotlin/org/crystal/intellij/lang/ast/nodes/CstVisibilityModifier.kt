package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.psi.CrVisibility

class CstVisibilityModifier(
    val visibility: CrVisibility,
    val exp: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstVisibilityModifier

        if (visibility != other.visibility) return false
        if (exp != other.exp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = visibility.hashCode()
        result = 31 * result + exp.hashCode()
        return result
    }

    override fun toString() = "VisibilityModifier($visibility, $exp)"
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.psi.CrVisibility

class CstVisibilityModifier(
    val visibility: CrVisibility,
    val exp: CstNode<*>,
    location: CstLocation? = null
) : CstNode<CstVisibilityModifier>(location) {
    fun copy(
        visibility: CrVisibility = this.visibility,
        exp: CstNode<*> = this.exp,
        location: CstLocation? = this.location
    ) = CstVisibilityModifier(visibility, exp, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

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

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitVisibilityModifier(this)

    override fun acceptChildren(visitor: CstVisitor) {
        exp.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformVisibilityModifier(this)
}
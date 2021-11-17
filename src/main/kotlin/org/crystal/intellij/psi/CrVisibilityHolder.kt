package org.crystal.intellij.psi

interface CrVisibilityHolder : CrExpression {
    val visibilityModifier: CrVisibilityModifier?
        get() = firstChild as? CrVisibilityModifier

    val visibility: CrVisibility?
        get() = visibilityModifier?.visibility
}
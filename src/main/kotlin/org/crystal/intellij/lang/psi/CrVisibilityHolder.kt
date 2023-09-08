package org.crystal.intellij.lang.psi

interface CrVisibilityHolder : CrExpression {
    val visibilityModifier: CrVisibilityModifier?
        get() = firstChild as? CrVisibilityModifier

    val visibility: CrVisibility?
        get() = visibilityModifier?.visibility
}
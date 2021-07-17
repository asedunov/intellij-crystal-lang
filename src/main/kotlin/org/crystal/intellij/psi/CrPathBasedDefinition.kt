package org.crystal.intellij.psi

sealed interface CrPathBasedDefinition : CrDefinition, CrPathNameElementHolder {
    override val nameElement: CrPathNameElement?
        get() = childOfType()
}
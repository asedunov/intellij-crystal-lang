package org.crystal.intellij.psi

interface CrPathBasedDefinition : CrDefinition, CrPathNameElementHolder {
    override val nameElement: CrPathNameElement?
        get() = childOfType()
}
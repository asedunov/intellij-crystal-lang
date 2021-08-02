package org.crystal.intellij.psi

sealed interface CrPathNameElementHolder : CrNameElementHolder {
    override val nameElement: CrPathNameElement?
        get() = stubChildOfType()
}
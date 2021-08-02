package org.crystal.intellij.psi

sealed interface CrSimpleNameElementHolder : CrNameElementHolder {
    override val nameElement: CrSimpleNameElement?
        get() = stubChildOfType()
}
package org.crystal.intellij.lang.psi

sealed interface CrSimpleNameElementHolder : CrNameElementHolder {
    override val nameElement: CrSimpleNameElement?
        get() = stubChildOfType()
}
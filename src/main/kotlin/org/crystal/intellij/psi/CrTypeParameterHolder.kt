package org.crystal.intellij.psi

sealed interface CrTypeParameterHolder : CrElement {
    val typeParameterList: CrTypeParameterList?
        get() = stubChildOfType()
}
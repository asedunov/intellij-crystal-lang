package org.crystal.intellij.lang.psi

sealed interface CrTypeParameterHolder : CrElement {
    val isGeneric: Boolean
        get() = typeParameterList != null

    val typeParameterList: CrTypeParameterList?
        get() = stubChildOfType()
}
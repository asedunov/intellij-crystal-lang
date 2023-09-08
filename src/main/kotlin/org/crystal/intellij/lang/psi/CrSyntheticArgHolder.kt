package org.crystal.intellij.lang.psi

sealed interface CrSyntheticArgHolder : CrElement {
    val syntheticArg: CrSyntheticArg?
        get() = childOfType()
}
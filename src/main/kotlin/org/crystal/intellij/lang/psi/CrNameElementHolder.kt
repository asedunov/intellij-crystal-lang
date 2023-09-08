package org.crystal.intellij.lang.psi

sealed interface CrNameElementHolder : CrElement {
    val nameElement: CrNameElement?
}
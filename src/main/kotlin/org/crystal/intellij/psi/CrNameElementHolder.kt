package org.crystal.intellij.psi

sealed interface CrNameElementHolder : CrElement {
    val nameElement: CrNameElement?
}
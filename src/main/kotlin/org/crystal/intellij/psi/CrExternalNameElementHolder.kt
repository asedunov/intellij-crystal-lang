package org.crystal.intellij.psi

sealed interface CrExternalNameElementHolder : CrElement {
    val externalNameElement: CrSimpleNameElement?
}
package org.crystal.intellij.lang.psi

sealed interface CrExternalNameElementHolder : CrElement {
    val externalNameElement: CrSimpleNameElement?
}
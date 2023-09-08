package org.crystal.intellij.lang.psi

sealed interface CrStringValueHolder : CrElement {
    val stringValue: String?
}
package org.crystal.intellij.psi

sealed interface CrStringValueHolder : CrElement {
    val stringValue: String?
}
package org.crystal.intellij.psi

sealed interface CrBodyHolder : CrDefinition {
    val body: CrBody?
        get() = childOfType()
}
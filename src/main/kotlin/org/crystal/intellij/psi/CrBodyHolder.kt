package org.crystal.intellij.psi

sealed interface CrBodyHolder : CrDefinitionWithFqName {
    val body: CrBody?
        get() = childOfType()
}
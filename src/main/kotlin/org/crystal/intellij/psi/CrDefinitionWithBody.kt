package org.crystal.intellij.psi

sealed interface CrDefinitionWithBody : CrDefinitionWithFqName {
    val body: CrBody?
        get() = childOfType()
}
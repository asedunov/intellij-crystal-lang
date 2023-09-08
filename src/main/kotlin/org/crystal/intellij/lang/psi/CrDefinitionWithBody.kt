package org.crystal.intellij.lang.psi

sealed interface CrDefinitionWithBody : CrDefinitionWithFqName {
    val body: CrBody?
        get() = childOfType()
}
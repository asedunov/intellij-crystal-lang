package org.crystal.intellij.psi

sealed interface CrTypedDefinition : CrDefinition {
    val type: CrType?
        get() = childOfType()
}
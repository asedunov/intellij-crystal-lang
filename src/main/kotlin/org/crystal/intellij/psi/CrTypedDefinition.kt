package org.crystal.intellij.psi

sealed interface CrTypedDefinition : CrDefinition {
    val type: CrTypeElement<*>?
        get() = childOfType()
}
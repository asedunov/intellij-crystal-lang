package org.crystal.intellij.lang.psi

sealed interface CrTypedDefinition : CrDefinition {
    val type: CrTypeElement<*>?
        get() = childOfType()
}
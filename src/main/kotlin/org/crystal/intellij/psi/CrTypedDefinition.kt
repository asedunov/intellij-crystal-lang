package org.crystal.intellij.psi

interface CrTypedDefinition : CrDefinition {
    val type: CrType?
        get() = childOfType()
}
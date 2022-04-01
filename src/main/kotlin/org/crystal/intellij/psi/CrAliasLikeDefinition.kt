package org.crystal.intellij.psi

sealed interface CrAliasLikeDefinition : CrTypeDefinition {
    val rhsType: CrType?
        get() = childOfType()
}
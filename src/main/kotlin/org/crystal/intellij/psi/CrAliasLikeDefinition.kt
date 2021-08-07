package org.crystal.intellij.psi

sealed interface CrAliasLikeDefinition : CrTypeDefinition, CrDefinitionWithFqName {
    val rhsType: CrType?
        get() = childOfType()
}
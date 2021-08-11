package org.crystal.intellij.psi

sealed interface CrAliasLikeDefinition : CrDefinitionWithFqName {
    val rhsType: CrType?
        get() = childOfType()
}
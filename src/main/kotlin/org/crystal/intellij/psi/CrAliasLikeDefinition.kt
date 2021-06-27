package org.crystal.intellij.psi

interface CrAliasLikeDefinition : CrDefinition {
    val rhsType: CrType?
        get() = childOfType()
}
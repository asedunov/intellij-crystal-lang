package org.crystal.intellij.lang.psi

sealed interface CrAliasLikeDefinition : CrTypeDefinition {
    val rhsType: CrTypeElement<*>?
        get() = childOfType()
}
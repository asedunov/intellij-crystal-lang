package org.crystal.intellij.psi

sealed interface CrFunctionLikeDefinition : CrDefinitionWithFqName {
    val parameterList: CrParameterList?
        get() = childOfType()

    val returnType: CrType?
        get() = childOfType()
}
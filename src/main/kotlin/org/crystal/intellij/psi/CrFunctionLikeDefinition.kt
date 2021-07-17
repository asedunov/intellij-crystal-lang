package org.crystal.intellij.psi

sealed interface CrFunctionLikeDefinition : CrDefinition {
    val parameterList: CrParameterList?
        get() = childOfType()

    val returnType: CrType?
        get() = childOfType()
}
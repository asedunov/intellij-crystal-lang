package org.crystal.intellij.psi

sealed interface CrDefinitionWithDefault : CrTypedDefinition {
    val defaultValue: CrExpression?
        get() = childOfType()
}
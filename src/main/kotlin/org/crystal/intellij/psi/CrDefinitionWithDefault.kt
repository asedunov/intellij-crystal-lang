package org.crystal.intellij.psi

interface CrDefinitionWithDefault : CrTypedDefinition {
    val defaultValue: CrExpression?
        get() = childOfType()
}
package org.crystal.intellij.psi

interface CrTypeDefinitionWithBody : CrDefinition {
    val body: CrTypeBody?
        get() = childOfType()
}
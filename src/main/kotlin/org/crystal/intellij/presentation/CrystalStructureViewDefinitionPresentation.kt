package org.crystal.intellij.presentation

import org.crystal.intellij.psi.CrDefinition
import org.crystal.intellij.psi.CrPathBasedDefinition

class CrystalStructureViewDefinitionPresentation(definition: CrDefinition) : CrystalDefinitionPresentationBase(definition) {
    override fun getLocationString(): String? {
        val pathBasedDefinition = definition as? CrPathBasedDefinition ?: return null
        val qualifiedName = pathBasedDefinition.localFqName?.parent?.fullName ?: return null
        return buildString {
            append("in ")
            if (definition.nameElement?.isGlobal == true) append("::")
            append(qualifiedName)
        }
    }
}
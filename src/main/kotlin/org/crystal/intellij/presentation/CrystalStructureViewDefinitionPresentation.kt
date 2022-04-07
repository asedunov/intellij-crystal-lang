package org.crystal.intellij.presentation

import org.crystal.intellij.psi.CrDefinition
import org.crystal.intellij.psi.CrTypeDefinition

class CrystalStructureViewDefinitionPresentation(definition: CrDefinition) : CrystalDefinitionPresentationBase(definition) {
    override fun getLocationString(): String? {
        val path = (definition as? CrTypeDefinition)?.nameElement?.qualifier ?: return null
        return buildString {
            append("in ").appendPath(path)
        }
    }
}
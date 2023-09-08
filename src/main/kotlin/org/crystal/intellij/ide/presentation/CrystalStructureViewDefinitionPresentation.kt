package org.crystal.intellij.ide.presentation

import org.crystal.intellij.lang.psi.CrDefinition
import org.crystal.intellij.lang.psi.CrTypeDefinition

class CrystalStructureViewDefinitionPresentation(definition: CrDefinition) : CrystalDefinitionPresentationBase(definition) {
    override fun getLocationString(): String? {
        val path = (definition as? CrTypeDefinition)?.nameElement?.qualifier ?: return null
        return buildString {
            append("in ").appendPath(path)
        }
    }
}
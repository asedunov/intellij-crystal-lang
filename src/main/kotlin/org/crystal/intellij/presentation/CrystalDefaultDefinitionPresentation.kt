package org.crystal.intellij.presentation

import org.crystal.intellij.psi.CrDefinition

class CrystalDefaultDefinitionPresentation(definition: CrDefinition) : CrystalDefinitionPresentationBase(definition) {
    override fun getLocationString(): String = definition.locationString
}
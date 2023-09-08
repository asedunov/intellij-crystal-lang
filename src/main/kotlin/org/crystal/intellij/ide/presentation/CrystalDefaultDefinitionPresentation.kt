package org.crystal.intellij.ide.presentation

import org.crystal.intellij.lang.psi.CrDefinition

class CrystalDefaultDefinitionPresentation(definition: CrDefinition) : CrystalDefinitionPresentationBase(definition) {
    override fun getLocationString(): String = definition.locationString
}
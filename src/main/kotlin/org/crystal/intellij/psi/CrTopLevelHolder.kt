package org.crystal.intellij.psi

import org.crystal.intellij.resolve.resolveFacade

sealed interface CrTopLevelHolder : CrSymbolOrdinalHolder {
    override fun ordinal() = project.resolveFacade.programLayout.getTopLevelOrdinal(this)
}
package org.crystal.intellij.lang.psi

import org.crystal.intellij.lang.resolve.resolveFacade

sealed interface CrTopLevelHolder : CrSymbolOrdinalHolder {
    override fun ordinal() = project.resolveFacade.programLayout.getTopLevelOrdinal(this)
}
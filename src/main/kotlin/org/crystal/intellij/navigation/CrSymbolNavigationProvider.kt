package org.crystal.intellij.navigation

import com.intellij.model.Symbol
import com.intellij.navigation.SymbolNavigationProvider
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.navigation.NavigationTarget
import org.crystal.intellij.psi.CrTypeDefinition
import org.crystal.intellij.resolve.symbols.CrSym

@Suppress("UnstableApiUsage")
class CrSymbolNavigationProvider : SymbolNavigationProvider {
    override fun getNavigationTargets(project: Project, symbol: Symbol): Collection<NavigationTarget> {
        val typeSym = symbol as? CrSym<*> ?: return emptyList()
        val explicitDefs = typeSym.sources.filterIsInstance<CrTypeDefinition>()
        val sourcesToShow = explicitDefs.ifEmpty { typeSym.sources }
        return sourcesToShow.map { CrTypePathNavigationTarget(typeSym, it) }
    }
}
package org.crystal.intellij.ide.navigation

import com.intellij.model.Symbol
import com.intellij.navigation.NavigationTarget
import com.intellij.navigation.SymbolNavigationProvider
import com.intellij.openapi.project.Project
import org.crystal.intellij.lang.psi.CrTypeDefinition
import org.crystal.intellij.lang.resolve.symbols.CrSym

@Suppress("UnstableApiUsage")
class CrSymbolNavigationProvider : SymbolNavigationProvider {
    override fun getNavigationTargets(project: Project, symbol: Symbol): Collection<NavigationTarget> {
        val typeSym = symbol as? CrSym<*> ?: return emptyList()
        val explicitDefs = typeSym.sources.filterIsInstance<CrTypeDefinition>()
        val sourcesToShow = explicitDefs.ifEmpty { typeSym.sources }
        return sourcesToShow.map { CrTypePathNavigationTarget(typeSym, it) }
    }
}
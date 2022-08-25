package org.crystal.intellij.search

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.search.ProjectAndLibrariesScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.ExtensibleQueryFactory
import com.intellij.util.QueryExecutor
import org.crystal.intellij.resolve.symbols.CrMacroSym

private val EP_NAME = ExtensionPointName<QueryExecutor<CrMacroSym, CrystalOverridingMacroSearch.Parameters>>(
    "org.crystal.overridingMacroSearch"
)

object CrystalOverridingMacroSearch : ExtensibleQueryFactory<CrMacroSym, CrystalOverridingMacroSearch.Parameters>(EP_NAME) {
    fun search(
        macro: CrMacroSym,
        searchScope: SearchScope = ProjectAndLibrariesScope(macro.program.project)
    ) = createUniqueResultsQuery(
        Parameters(macro, searchScope)
    )

    data class Parameters(
        val macro: CrMacroSym,
        val searchScope: SearchScope
    )
}
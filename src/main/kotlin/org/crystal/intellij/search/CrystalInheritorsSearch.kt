package org.crystal.intellij.search

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.search.ProjectAndLibrariesScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.ExtensibleQueryFactory
import com.intellij.util.QueryExecutor
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym

private val EP_NAME = ExtensionPointName<QueryExecutor<CrModuleLikeSym, CrystalInheritorsSearch.Parameters>>(
    "org.crystal.classInheritorsSearch"
)

object CrystalInheritorsSearch : ExtensibleQueryFactory<CrModuleLikeSym, CrystalInheritorsSearch.Parameters>(EP_NAME) {
    fun search(
        superClass: CrModuleLikeSym,
        checkDeepInheritance: Boolean,
        searchScope: SearchScope = ProjectAndLibrariesScope(superClass.program.project)
    ) = createUniqueResultsQuery(
        Parameters(superClass, checkDeepInheritance, searchScope)
    )

    data class Parameters(
        val rootSym: CrModuleLikeSym,
        val checkDeep: Boolean,
        val searchScope: SearchScope
    )
}
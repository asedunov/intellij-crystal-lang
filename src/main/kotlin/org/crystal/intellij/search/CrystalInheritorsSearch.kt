/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
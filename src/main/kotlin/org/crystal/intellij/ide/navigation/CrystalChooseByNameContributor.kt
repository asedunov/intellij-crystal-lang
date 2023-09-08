package org.crystal.intellij.ide.navigation

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import org.crystal.intellij.lang.psi.CrElement

abstract class CrystalChooseByNameContributor : ChooseByNameContributorEx {
    protected fun StubIndexKey<String, *>.processKeys(
        processor: Processor<in String>,
        scope: GlobalSearchScope,
        filter: IdFilter?
    ) {
        StubIndex.getInstance().processAllKeys(this, processor, scope, filter)
    }

    protected inline fun <reified Psi : CrElement> StubIndexKey<String, Psi>.processElements(
        name: String,
        processor: Processor<in NavigationItem>,
        parameters: FindSymbolParameters
    ) {
        val project = parameters.searchScope.project ?: return
        StubIndex.getInstance().processElements(
            this,
            name,
            project,
            parameters.searchScope,
            parameters.idFilter,
            Psi::class.java,
            processor
        )
    }
}
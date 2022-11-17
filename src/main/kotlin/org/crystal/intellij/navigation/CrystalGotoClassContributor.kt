package org.crystal.intellij.navigation

import com.intellij.navigation.GotoClassContributor
import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import org.crystal.intellij.psi.CrDefinition
import org.crystal.intellij.psi.CrDefinitionWithFqName
import org.crystal.intellij.stubs.indexes.CrystalTypeShortNameIndex

class CrystalGotoClassContributor : CrystalChooseByNameContributor(), GotoClassContributor {
    override fun getQualifiedName(item: NavigationItem): String? {
        val definition = item as? CrDefinition ?: return null
        return if (definition is CrDefinitionWithFqName) definition.fqName?.fullName else definition.name
    }

    override fun getQualifiedNameSeparator() = "::"

    override fun processNames(processor: Processor<in String>, scope: GlobalSearchScope, filter: IdFilter?) {
        CrystalTypeShortNameIndex.key.processKeys(processor, scope, filter)
    }

    override fun processElementsWithName(
        name: String,
        processor: Processor<in NavigationItem>,
        parameters: FindSymbolParameters
    ) {
        CrystalTypeShortNameIndex.key.processElements(name, processor, parameters)
    }
}
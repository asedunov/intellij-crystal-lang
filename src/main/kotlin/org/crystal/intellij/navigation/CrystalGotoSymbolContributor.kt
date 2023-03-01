package org.crystal.intellij.navigation

import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import org.crystal.intellij.stubs.indexes.CrystalFunctionShortNameIndex
import org.crystal.intellij.stubs.indexes.CrystalStrictConstantShortNameIndex
import org.crystal.intellij.stubs.indexes.CrystalTypeShortNameIndex
import org.crystal.intellij.stubs.indexes.CrystalVariableShortNameIndex

class CrystalGotoSymbolContributor : CrystalChooseByNameContributor() {
    override fun processNames(processor: Processor<in String>, scope: GlobalSearchScope, filter: IdFilter?) {
        CrystalTypeShortNameIndex.key.processKeys(processor, scope, filter)
        CrystalFunctionShortNameIndex.key.processKeys(processor, scope, filter)
        CrystalVariableShortNameIndex.key.processKeys(processor, scope, filter)
        CrystalStrictConstantShortNameIndex.key.processKeys(processor, scope, filter)
    }

    override fun processElementsWithName(
        name: String,
        processor: Processor<in NavigationItem>,
        parameters: FindSymbolParameters
    ) {
        CrystalTypeShortNameIndex.key.processElements(name, processor, parameters)
        CrystalFunctionShortNameIndex.key.processElements(name, processor, parameters)
        CrystalVariableShortNameIndex.key.processElements(name, processor, parameters)
        CrystalStrictConstantShortNameIndex.key.processElements(name, processor, parameters)
    }
}
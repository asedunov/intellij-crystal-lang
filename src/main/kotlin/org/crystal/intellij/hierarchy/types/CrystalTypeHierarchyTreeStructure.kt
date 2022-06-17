package org.crystal.intellij.hierarchy.types

import com.intellij.util.SmartList
import org.crystal.intellij.psi.CrFakeTypeDefinition
import org.crystal.intellij.psi.CrConstantSource
import org.crystal.intellij.resolve.symbols.CrClassLikeSym

class CrystalTypeHierarchyTreeStructure(
    element: CrConstantSource,
    currentScopeType: String
) : CrystalSubtypesHierarchyTreeStructure(
    element.project,
    buildBaseDescriptor(element),
    currentScopeType
) {
    init {
        setBaseElement(myBaseDescriptor)
    }
}

private fun buildBaseDescriptor(element: CrConstantSource): CrystalTypeHierarchyNodeDescriptor? {
    val symbols = findSuperClasses(element)
    if (symbols.isEmpty()) return CrystalTypeHierarchyNodeDescriptor(element, null, true)
    var descriptor: CrystalTypeHierarchyNodeDescriptor? = null
    for (i in symbols.indices.reversed()) {
        val symbol = symbols[i]
        val source = symbol.sources.firstOrNull() ?: CrFakeTypeDefinition(element, symbol)
        val newDescriptor = CrystalTypeHierarchyNodeDescriptor(source, descriptor, i == 0)
        descriptor?.cachedChildren = arrayOf(newDescriptor)
        descriptor = newDescriptor
    }
    return descriptor
}

private fun findSuperClasses(element: CrConstantSource): List<CrClassLikeSym> {
    var symbol = element.resolveSymbol() as? CrClassLikeSym ?: return emptyList()
    val result = SmartList<CrClassLikeSym>()
    while (true) {
        if (symbol in result) break
        result += symbol
        symbol = symbol.superClass ?: break
    }
    return result
}
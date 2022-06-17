package org.crystal.intellij.hierarchy.types

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.ide.hierarchy.HierarchyTreeStructure
import com.intellij.openapi.project.Project
import com.intellij.util.ArrayUtilRt
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.psi.CrFakeTypeDefinition
import org.crystal.intellij.psi.CrConstantSource
import org.crystal.intellij.resolve.CrStdFqNames
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.search.CrystalInheritorsSearch

open class CrystalSubtypesHierarchyTreeStructure protected constructor(
    project: Project,
    parentDescriptor: CrystalTypeHierarchyNodeDescriptor?,
    private val currentScopeType: String
) : HierarchyTreeStructure(project, parentDescriptor) {
    constructor(
        element: CrConstantSource,
        currentScopeType: String
    ): this(
        element.project,
        CrystalTypeHierarchyNodeDescriptor(element, null, true),
        currentScopeType
    )

    override fun buildChildren(descriptor: HierarchyNodeDescriptor): Array<Any> {
        descriptor as CrystalTypeHierarchyNodeDescriptor
        val symbol = descriptor.symbol as? CrModuleLikeSym
            ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        when (symbol.fqName) {
            CrStdFqNames.OBJECT -> return arrayOf(CrystalBundle.message("node.hierarchy.std.object"))
            CrStdFqNames.REFERENCE -> return arrayOf(CrystalBundle.message("node.hierarchy.std.reference"))
            CrStdFqNames.VALUE -> return arrayOf(CrystalBundle.message("node.hierarchy.std.value"))
            CrStdFqNames.STRUCT -> return arrayOf(CrystalBundle.message("node.hierarchy.std.struct"))
            else -> {}
        }
        val element = descriptor.psiElement ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val searchScope = getSearchScope(currentScopeType, element)
        return CrystalInheritorsSearch
            .search(symbol, false, searchScope)
            .mapNotNull {
                val source = it.sources.firstOrNull() ?: CrFakeTypeDefinition(element, it)
                CrystalTypeHierarchyNodeDescriptor(source, descriptor, false)
            }
            .toTypedArray()
    }
}

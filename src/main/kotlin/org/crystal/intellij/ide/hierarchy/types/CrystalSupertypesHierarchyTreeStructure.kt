package org.crystal.intellij.ide.hierarchy.types

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.ide.hierarchy.HierarchyTreeStructure
import com.intellij.util.ArrayUtilRt
import com.intellij.util.SmartList
import org.crystal.intellij.lang.psi.CrFakeTypeDefinition
import org.crystal.intellij.lang.psi.CrConstantSource
import org.crystal.intellij.lang.resolve.scopes.asSequence
import org.crystal.intellij.lang.resolve.symbols.CrModuleLikeSym

class CrystalSupertypesHierarchyTreeStructure(
    element: CrConstantSource
) : HierarchyTreeStructure(element.project, CrystalTypeHierarchyNodeDescriptor(element, null, true)) {
    override fun buildChildren(descriptor: HierarchyNodeDescriptor): Array<Any> {
        return ((descriptor as CrystalTypeHierarchyNodeDescriptor).symbol as? CrModuleLikeSym)
            ?.parents
            ?.asSequence()
            ?.mapNotNullTo(SmartList()) {
                val parentPsi = descriptor.psiElement ?: return@mapNotNullTo null
                val superPsi = it.sources.firstOrNull() ?: CrFakeTypeDefinition(parentPsi, it)
                CrystalTypeHierarchyNodeDescriptor(superPsi, descriptor, false)
            }
            ?.toTypedArray() ?: ArrayUtilRt.EMPTY_OBJECT_ARRAY
    }
}
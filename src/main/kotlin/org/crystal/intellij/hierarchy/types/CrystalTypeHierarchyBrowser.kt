package org.crystal.intellij.hierarchy.types

import com.intellij.ide.hierarchy.HierarchyBrowserManager
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.ide.hierarchy.HierarchyTreeStructure
import com.intellij.ide.hierarchy.TypeHierarchyBrowserBase
import com.intellij.ide.util.treeView.AlphaComparator
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.util.Comparing
import com.intellij.psi.PsiElement
import org.crystal.intellij.psi.CrModule
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.psi.CrTypeDefinition
import org.crystal.intellij.psi.CrConstantSource
import java.text.MessageFormat
import javax.swing.JPanel
import javax.swing.JTree

class CrystalTypeHierarchyBrowser(element: CrConstantSource) : TypeHierarchyBrowserBase(element.project, element) {
    override fun isInterface(psiElement: PsiElement): Boolean {
        return psiElement is CrPathNameElement || psiElement is CrModule
    }

    override fun createTrees(trees: MutableMap<in String, in JTree>) {
        createTreeAndSetupCommonActions(trees, IdeActions.GROUP_TYPE_HIERARCHY_POPUP)
    }

    override fun prependActions(actionGroup: DefaultActionGroup) {
        super.prependActions(actionGroup)
        actionGroup.add(object : ChangeScopeAction() {
            override fun isEnabled() = !Comparing.strEqual(currentViewType, getSupertypesHierarchyType())
        })
    }

    override fun getContentDisplayName(typeName: String, element: PsiElement): String? {
        val source = element as? CrConstantSource ?: return null
        return MessageFormat.format(typeName, source.presentableTextForHierarchy())
    }

    override fun getElementFromDescriptor(descriptor: HierarchyNodeDescriptor): PsiElement? {
        return (descriptor as? CrystalTypeHierarchyNodeDescriptor)?.psiElement
    }

    override fun createLegendPanel(): JPanel? = null

    override fun isApplicableElement(element: PsiElement) = element is CrConstantSource

    override fun getComparator(): Comparator<NodeDescriptor<*>>? {
        val state = HierarchyBrowserManager.getInstance(myProject).state
        return if (state != null && state.SORT_ALPHABETICALLY) AlphaComparator.INSTANCE else DEFAULT_HIERARCHY_NODE_COMPARATOR
    }

    override fun createHierarchyTreeStructure(type: String, psiElement: PsiElement): HierarchyTreeStructure? {
        val element = psiElement as CrConstantSource
        return when (type) {
            getSupertypesHierarchyType() -> CrystalSupertypesHierarchyTreeStructure(element)
            getSubtypesHierarchyType() -> CrystalSubtypesHierarchyTreeStructure(element, currentScopeType)
            getTypeHierarchyType() -> CrystalTypeHierarchyTreeStructure(element, currentScopeType)
            else -> null
        }
    }

    override fun canBeDeleted(psiElement: PsiElement?) = psiElement is CrTypeDefinition

    override fun getQualifiedName(psiElement: PsiElement?): String {
        return (psiElement as? CrConstantSource)?.fqName?.fullName ?: ""
    }
}
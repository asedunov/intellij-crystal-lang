package org.crystal.intellij.ide.hierarchy.types

import com.intellij.ide.hierarchy.HierarchyBrowser
import com.intellij.ide.hierarchy.HierarchyProvider
import com.intellij.ide.hierarchy.TypeHierarchyBrowserBase
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import org.crystal.intellij.lang.psi.CrConstantSource

class CrystalTypeHierarchyProvider : HierarchyProvider {
    override fun getTarget(dataContext: DataContext): PsiElement? {
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return null
        val editor = CommonDataKeys.EDITOR.getData(dataContext)
            ?: return CommonDataKeys.PSI_ELEMENT.getData(dataContext) as? CrConstantSource
        val file = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return null
        val element = file.findElementAt(editor.caretModel.offset) ?: return null
        val typeSource = element.parentOfType<CrConstantSource>() ?: return null
        return typeSource.resolveSymbol()?.sources?.firstOrNull()
    }

    override fun createHierarchyBrowser(target: PsiElement): HierarchyBrowser {
        return CrystalTypeHierarchyBrowser(target as CrConstantSource)
    }

    override fun browserActivated(hierarchyBrowser: HierarchyBrowser) {
        val browser = hierarchyBrowser as CrystalTypeHierarchyBrowser
        val typeName = if (browser.isInterface) {
            TypeHierarchyBrowserBase.getSubtypesHierarchyType()
        } else {
            TypeHierarchyBrowserBase.getTypeHierarchyType()
        }
        browser.changeView(typeName)
    }
}
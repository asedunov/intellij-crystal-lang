package org.crystal.intellij.ide.structure

import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import org.crystal.intellij.lang.psi.CrFile

class CrystalStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        return if (psiFile is CrFile) {
            object : TreeBasedStructureViewBuilder() {
                override fun createStructureViewModel(editor: Editor?) = CrystalStructureViewModel(editor, psiFile)
            }
        } else null
    }
}
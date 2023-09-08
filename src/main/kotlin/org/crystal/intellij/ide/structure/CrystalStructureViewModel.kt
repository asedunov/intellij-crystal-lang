package org.crystal.intellij.ide.structure

import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModel.ExpandInfoProvider
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.editor.Editor
import org.crystal.intellij.lang.psi.*

class CrystalStructureViewModel(
    editor: Editor?,
    file: CrFile
) : TextEditorBasedStructureViewModel(editor, file), StructureViewModel.ElementInfoProvider, ExpandInfoProvider {
    override fun getPsiFile() = super.getPsiFile() as CrFile

    override fun getRoot(): StructureViewTreeElement = CrystalStructureViewElement(psiFile)

    override fun getSuitableClasses() = arrayOf(CrDefinition::class.java)

    override fun getSorters() = arrayOf(Sorter.ALPHA_SORTER)

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?) = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement?): Boolean {
        val obj = element?.value ?: return false
        return obj is CrEnumConstant ||
                obj is CrFunction ||
                obj is CrMethod ||
                obj is CrTypeDef ||
                obj is CrVariable ||
                obj is CrAnnotation ||
                obj is CrAlias ||
                obj is CrCField
    }

    override fun isAutoExpand(element: StructureViewTreeElement) = false

    override fun isSmartExpand() = true
}
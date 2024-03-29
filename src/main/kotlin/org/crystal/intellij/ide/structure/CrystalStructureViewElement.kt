package org.crystal.intellij.ide.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.navigation.ItemPresentation
import org.crystal.intellij.ide.presentation.CrystalStructureViewDefinitionPresentation
import org.crystal.intellij.lang.psi.*
import javax.swing.Icon

class CrystalStructureViewElement(element: CrElement) : PsiTreeElementBase<CrElement>(element) {
    private val presentation = if (element is CrDefinition)
        CrystalStructureViewDefinitionPresentation(element)
    else
        element.presentation

    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        val container = when (val element = element) {
            is CrFile -> element
            is CrDefinitionWithBody -> element.body
            else -> null
        } ?: return emptyList()
        return container
            .traverser()
            .expandAndSkip { it == container || it is CrCFieldGroup || it is CrFileFragment }
            .filter(CrDefinition::class.java)
            .filterNot { it is CrDefinitionWithFqName && it.isLocal }
            .map(::CrystalStructureViewElement)
            .toList()
    }

    override fun getPresentation(): ItemPresentation = presentation ?: this

    override fun getIcon(open: Boolean): Icon? = presentation?.getIcon(open)

    override fun getPresentableText(): String? = presentation?.presentableText

    override fun getLocationString() = presentation?.locationString

    override fun canNavigateToSource() = element?.canNavigateToSource() ?: false
}
package org.crystal.intellij.ide.hierarchy.types

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.roots.ui.util.CompositeAppearance
import com.intellij.psi.PsiElement
import org.crystal.intellij.ide.presentation.getIcon
import org.crystal.intellij.lang.psi.CrConstantSource
import org.crystal.intellij.lang.resolve.symbols.CrProperTypeSym
import java.awt.Font

class CrystalTypeHierarchyNodeDescriptor(
    element: CrConstantSource,
    parentDescriptor: NodeDescriptor<*>?,
    isBase: Boolean
) : HierarchyNodeDescriptor(element.project, parentDescriptor, element, isBase) {
    val source: CrConstantSource?
        get() = psiElement as? CrConstantSource

    val symbol: CrProperTypeSym?
        get() = source?.resolveSymbol() as? CrProperTypeSym

    override fun getIcon(element: PsiElement) = symbol?.getIcon()

    override fun update(): Boolean {
        val changed = super.update()

        val source = source ?: return invalidElement()

        if (changed && myIsBase) icon = getBaseMarkerIcon(icon)

        val oldText = myHighlightedText

        myHighlightedText = CompositeAppearance()

        var nameAttributes: TextAttributes? = null
        if (myColor != null) {
            nameAttributes = TextAttributes(myColor, null, null, null, Font.PLAIN)
        }
        myHighlightedText.ending.addText(
            source.presentableTextForHierarchy(),
            nameAttributes
        )

        myName = myHighlightedText.text

        return changed || myHighlightedText != oldText
    }
}
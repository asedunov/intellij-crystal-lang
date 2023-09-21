package org.crystal.intellij.lang.psi

import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.source.tree.CompositePsiElement
import org.crystal.intellij.lang.CrystalLanguage
import org.crystal.intellij.lang.parser.CrSyntheticArgElementType

class CrSyntheticArg(
    type: CrSyntheticArgElementType
) : CompositePsiElement(type), CrElement {
    override fun accept(visitor: PsiElementVisitor) = acceptElement(visitor)

    override fun accept(visitor: CrVisitor) = visitor.visitSyntheticArg(this)

    override fun getElementType() = super.getElementType() as CrSyntheticArgElementType

    override fun getName() = "__arg${elementType.id}"

    override fun getLanguage() = CrystalLanguage
}
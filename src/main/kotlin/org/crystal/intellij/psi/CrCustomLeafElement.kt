package org.crystal.intellij.psi

import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType

abstract class CrCustomLeafElement(type: IElementType, text: CharSequence) : LeafPsiElement(type, text), CrElement {
    override fun accept(visitor: PsiElementVisitor) = acceptElement(visitor)
}
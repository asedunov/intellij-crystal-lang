package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrMacroFragment(
    type: IElementType,
    text: CharSequence
) : CrCustomLeafElement(type, text), CrMacroLiteralElement {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroFragment(this)
}
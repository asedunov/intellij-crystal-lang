package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrStringRawElement(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text), CrElement {
    override fun accept(visitor: CrVisitor) = visitor.visitStringRawElement(this)
}
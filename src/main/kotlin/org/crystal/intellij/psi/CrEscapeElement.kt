package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

sealed class CrEscapeElement(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text), CrElement {
    override fun accept(visitor: CrVisitor) = visitor.visitEscapeElement(this)
}
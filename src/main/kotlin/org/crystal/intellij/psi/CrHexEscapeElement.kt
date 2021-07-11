package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrHexEscapeElement(type: IElementType, text: CharSequence) : CrEscapeElement(type, text) {
    override fun accept(visitor: CrVisitor) = visitor.visitHexEscapeElement(this)
}
package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrRawEscapeElement(type: IElementType, text: CharSequence) : CrEscapeElement(type, text) {
    override fun accept(visitor: CrVisitor) = visitor.visitRawEscapeElement(this)
}
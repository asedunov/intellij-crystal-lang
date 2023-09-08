package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrSimpleEscapeElement(type: IElementType, text: CharSequence) : CrEscapeElement(type, text) {
    override fun accept(visitor: CrVisitor) = visitor.visitSimpleEscapeElement(this)
}
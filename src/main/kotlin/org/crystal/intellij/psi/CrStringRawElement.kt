package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrStringRawElement(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text), CrStringValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitStringRawElement(this)

    override val stringValue: String
        get() = text
}
package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrRawEscapeElement(type: IElementType, text: CharSequence) : CrEscapeElement(type, text), CrStringValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitRawEscapeElement(this)

    override val stringValue: String
        get() = text.substring(1)
}
package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrHexEscapeElement(type: IElementType, text: CharSequence) : CrEscapeElement(type, text), CrCharValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitHexEscapeElement(this)

    override val charValue: Int?
        get() {
            if (text.length != 4) return null
            return text.substring(2).toInt(16)
        }
}
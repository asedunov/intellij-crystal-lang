package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrHexEscapeElement(type: IElementType, text: CharSequence) : CrEscapeElement(type, text), CrCharEscapeHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitHexEscapeElement(this)

    override val escapedChar: Char?
        get() {
            if (text.length != 4) return null
            return text.substring(2).toInt(16).toChar()
        }
}
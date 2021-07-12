package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrUnicodeEscapeElement(type: IElementType, text: CharSequence) : CrEscapeElement(type, text) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnicodeEscapeElement(this)

    val escapedChar: Char?
        get() {
            if (text.length != 6) return null
            return text.substring(2).toInt(16).toChar()
        }
}
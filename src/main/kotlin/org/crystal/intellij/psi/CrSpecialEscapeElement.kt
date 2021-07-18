package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrSpecialEscapeElement(type: IElementType, text: CharSequence) : CrEscapeElement(type, text), CrCharValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitSpecialEscapeElement(this)

    override val charValue: Char?
        get() = when (text[1]) {
            'a' -> 7.toChar()
            'b' -> '\b'
            'e' -> 27.toChar()
            'f' -> 12.toChar()
            'n' -> '\n'
            'r' -> '\r'
            't' -> '\t'
            'v' -> 11.toChar()
            else -> null
        }
}
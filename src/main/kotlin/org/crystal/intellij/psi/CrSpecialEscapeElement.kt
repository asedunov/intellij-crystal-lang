package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrSpecialEscapeElement(type: IElementType, text: CharSequence) : CrEscapeElement(type, text), CrCharValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitSpecialEscapeElement(this)

    override val charValue: Int?
        get() = when (text[1]) {
            'a' -> 7
            'b' -> '\b'.code
            'e' -> 27
            'f' -> 12
            'n' -> '\n'.code
            'r' -> '\r'.code
            't' -> '\t'.code
            'v' -> 11
            else -> null
        }
}
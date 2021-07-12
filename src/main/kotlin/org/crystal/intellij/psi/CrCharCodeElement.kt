package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrCharCodeElement(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text), CrCharEscapeHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCharCodeElement(this)

    override val escapedChar: Char?
        get() {
            val code = text.toInt(16)
            return if (code <= 0x10FFFF) code.toChar() else null
        }
}
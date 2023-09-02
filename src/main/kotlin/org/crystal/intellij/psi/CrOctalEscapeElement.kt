package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrOctalEscapeElement(type: IElementType, text: CharSequence) : CrEscapeElement(type, text), CrCharValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitOctalEscapeElement(this)

    override val charValue: Int
        get() = text.substring(1).toInt(8)
}
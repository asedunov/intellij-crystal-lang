package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrCharCodeElement(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text), CrCharValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCharCodeElement(this)

    override val charValue: Int?
        get() = text.toInt(16).takeIf { it <= 0x10FFFF }
}
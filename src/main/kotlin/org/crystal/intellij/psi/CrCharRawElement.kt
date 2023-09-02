package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrCharRawElement(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text), CrCharValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCharRawElement(this)

    override val charValue: Int
        get() = text.codePointAt(0)
}
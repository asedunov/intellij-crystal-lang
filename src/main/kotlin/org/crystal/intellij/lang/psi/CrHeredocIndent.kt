package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrHeredocIndent(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text) {
    override fun accept(visitor: CrVisitor) = visitor.visitHeredocIndent(this)

    val size: Int
        get() = textLength
}
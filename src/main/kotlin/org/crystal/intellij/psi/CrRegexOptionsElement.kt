package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrRegexOptionsElement(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text) {
    override fun accept(visitor: CrVisitor) = visitor.visitRegexOptionsElement(this)

    val options: String
        get() = text
}
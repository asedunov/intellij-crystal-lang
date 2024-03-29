package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrRegexOptionsElement(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text) {
    override fun accept(visitor: CrVisitor) = visitor.visitRegexOptionsElement(this)
}
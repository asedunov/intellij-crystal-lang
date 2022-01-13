package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrGlobalMatchIndexElement(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text), CrElement {
    override fun accept(visitor: CrVisitor) = visitor.visitGlobalMatchDataIndex(this)

    val index: Int?
        get() {
            val text = text
            val end = if (text.endsWith('?')) text.length - 1 else text.length
            return text.substring(1, end).toIntOrNull()
        }
}
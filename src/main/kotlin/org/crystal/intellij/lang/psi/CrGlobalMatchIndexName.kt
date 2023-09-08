package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrGlobalMatchIndexName(type: IElementType, text: CharSequence) : CrNameLeafElement(type, text), CrElement {
    override fun accept(visitor: CrVisitor) = visitor.visitGlobalMatchDataIndexName(this)

    val index: Int?
        get() {
            val text = text
            val end = if (text.endsWith('?')) text.length - 1 else text.length
            return text.substring(1, end).toIntOrNull()
        }

    override val kind: CrNameKind
        get() = CrNameKind.GLOBAL_MATCH_INDEX
}
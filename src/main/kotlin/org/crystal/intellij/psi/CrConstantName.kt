package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

class CrConstantName(type: IElementType, text: CharSequence) : CrNameLeafElement(type, text) {
    override val kind: CrNameKind
        get() = CrNameKind.CONSTANT

    override fun getName() = text
}
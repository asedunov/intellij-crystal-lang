package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrPreviousDef(type: IElementType, text: CharSequence) : CrNameLeafElement(type, text) {
    override val kind: CrNameKind
        get() = CrNameKind.PREVIOUS_DEF
}
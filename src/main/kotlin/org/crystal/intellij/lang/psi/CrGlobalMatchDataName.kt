package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrGlobalMatchDataName(type: IElementType, text: CharSequence) : CrNameLeafElement(type, text), CrElement {
    override val kind: CrNameKind
        get() = CrNameKind.GLOBAL_MATCH_DATA
}
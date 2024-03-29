package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrMacroVariableName(type: IElementType, text: CharSequence) : CrNameLeafElement(type, text) {
    override val kind: CrNameKind
        get() = CrNameKind.MACRO_VARIABLE
}
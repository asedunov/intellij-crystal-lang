package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

open class CrClassVariableName(type: IElementType, text: CharSequence) : CrNameLeafElement(type, text) {
    override val kind: CrNameKind
        get() = CrNameKind.CLASS_VARIABLE
}
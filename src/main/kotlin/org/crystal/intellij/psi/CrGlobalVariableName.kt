package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

open class CrGlobalVariableName(type: IElementType, text: CharSequence) : CrNameLeafElement(type, text) {
    override val kind: CrNameKind
        get() = CrNameKind.GLOBAL_VARIABLE
}
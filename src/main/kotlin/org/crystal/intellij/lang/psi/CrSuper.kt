package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

class CrSuper(type: IElementType, text: CharSequence) : CrNameLeafElement(type, text) {
    override val kind: CrNameKind
        get() = CrNameKind.SUPER
}
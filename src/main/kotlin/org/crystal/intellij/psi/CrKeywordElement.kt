package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

open class CrKeywordElement(
    type: IElementType,
    text: CharSequence
) : CrCustomLeafElement(type, text), CrNameKindAware {
    override val kind: CrNameKind
        get() = CrNameKind.IDENTIFIER
}
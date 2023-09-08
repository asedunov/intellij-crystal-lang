package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

open class CrKeywordElement(
    type: IElementType,
    text: CharSequence
) : CrCustomLeafElement(type, text), CrNameKindAware {
    override val kind: CrNameKind
        get() = CrNameKind.IDENTIFIER
}
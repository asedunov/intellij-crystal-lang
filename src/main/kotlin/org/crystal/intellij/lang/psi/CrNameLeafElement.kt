package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType

abstract class CrNameLeafElement(
    type: IElementType,
    text: CharSequence
) : CrCustomLeafElement(type, text), CrNameKindAware
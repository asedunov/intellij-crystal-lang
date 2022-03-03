package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType

abstract class CrNameLeafElement(
    type: IElementType,
    text: CharSequence
) : CrCustomLeafElement(type, text), CrNameKindAware
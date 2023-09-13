package org.crystal.intellij.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.tree.IElementType

sealed class CrHeredocId(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text) {
    abstract fun resolveToPairedId(): CrHeredocId?

    abstract val nameRange: TextRange
}
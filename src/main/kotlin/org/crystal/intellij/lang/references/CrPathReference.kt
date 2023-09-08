package org.crystal.intellij.lang.references

import com.intellij.openapi.util.TextRange
import org.crystal.intellij.lang.psi.CrPathNameElement

class CrPathReference(element: CrPathNameElement): CrReferenceBase<CrPathNameElement>(element) {
    override fun computeRangeInElement(): TextRange {
        val anchor = element.item ?: return TextRange.EMPTY_RANGE
        return TextRange.from(anchor.startOffsetInParent, anchor.textLength)
    }
}
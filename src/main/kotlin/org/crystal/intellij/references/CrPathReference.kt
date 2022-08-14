package org.crystal.intellij.references

import com.intellij.openapi.util.TextRange
import org.crystal.intellij.psi.CrPathNameElement

class CrPathReference(element: CrPathNameElement): CrReferenceBase<CrPathNameElement>(element) {
    override fun computeRangeInElement(): TextRange {
        val anchor = element.item ?: return TextRange.EMPTY_RANGE
        return TextRange.from(anchor.startOffsetInParent, anchor.textLength)
    }
}
package org.crystal.intellij.lang.references

import com.intellij.openapi.util.TextRange
import org.crystal.intellij.lang.psi.CrSimpleNameElement

class CrSimpleReference(element: CrSimpleNameElement): CrReferenceBase<CrSimpleNameElement>(element) {
    override fun computeRangeInElement() = TextRange.from(0, element.textLength)
}
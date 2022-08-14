package org.crystal.intellij.references

import com.intellij.openapi.util.TextRange
import org.crystal.intellij.psi.CrSimpleNameElement

class CrSimpleReference(element: CrSimpleNameElement): CrReferenceBase<CrSimpleNameElement>(element) {
    override fun computeRangeInElement() = TextRange.from(0, element.textLength)
}
package org.crystal.intellij.references

import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import org.crystal.intellij.psi.CrPathNameElement

@Suppress("UnstableApiUsage")
class CrPathReference(
    private val element: CrPathNameElement
) : PsiSymbolReference {
    private val _rangeInElement: TextRange by lazy {
        val anchor = element.item ?: return@lazy TextRange.EMPTY_RANGE
        TextRange.from(anchor.startOffsetInParent, anchor.textLength)
    }

    override fun getElement() = element

    override fun getRangeInElement() = _rangeInElement

    override fun resolveReference() = listOfNotNull(element.resolveSymbol())

    override fun equals(other: Any?): Boolean =
        this === other || other is CrPathReference && element === other.element

    override fun hashCode(): Int =
        element.hashCode()
}
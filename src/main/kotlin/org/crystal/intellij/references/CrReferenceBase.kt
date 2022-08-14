package org.crystal.intellij.references

import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import org.crystal.intellij.psi.CrReferenceElement

@Suppress("UnstableApiUsage")
abstract class CrReferenceBase<T : CrReferenceElement>(
    private val element: T
) : PsiSymbolReference {
    private val _rangeInElement: TextRange by lazy(::computeRangeInElement)

    protected abstract fun computeRangeInElement(): TextRange

    override fun getElement() = element

    override fun getRangeInElement() = _rangeInElement

    override fun resolveReference() = listOfNotNull(element.resolveSymbol())

    override fun equals(other: Any?): Boolean =
        this === other || other is CrReferenceBase<*> && element === other.element

    override fun hashCode(): Int =
        element.hashCode()
}
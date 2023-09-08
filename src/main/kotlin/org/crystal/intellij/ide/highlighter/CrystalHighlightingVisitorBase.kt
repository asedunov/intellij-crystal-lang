package org.crystal.intellij.ide.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.crystal.intellij.lang.psi.CrNamedElement
import org.crystal.intellij.lang.psi.CrRecursiveVisitor

abstract class CrystalHighlightingVisitorBase(
    private val highlightInfos: MutableList<HighlightInfo>
) : CrRecursiveVisitor() {
    protected val CrNamedElement.defaultAnchor: PsiElement
        get() = nameElement ?: firstChild

    protected inline fun errorIf(anchor: PsiElement, description: String, validator: (String) -> String?) {
        error(anchor, validator(description) ?: return)
    }

    protected fun error(anchor: PsiElement, message: String, range: TextRange = anchor.textRange): HighlightInfo? {
        return highlight(anchor, message, HighlightInfoType.ERROR, range)
    }

    protected fun warning(anchor: PsiElement, message: String, range: TextRange = anchor.textRange): HighlightInfo? {
        return highlight(anchor, message, HighlightInfoType.WARNING, range)
    }

    protected fun deprecated(anchor: PsiElement, message: String, range: TextRange = anchor.textRange): HighlightInfo? {
        return highlight(anchor, message, HighlightInfoType.DEPRECATED, range)
    }

    protected fun highlight(
        anchor: PsiElement,
        message: String,
        type: HighlightInfoType,
        range: TextRange
    ): HighlightInfo? {
        val info = HighlightInfo
            .newHighlightInfo(type)
            .range(anchor, range.startOffset, range.endOffset)
            .descriptionAndTooltip(message)
            .create() ?: return null
        highlightInfos += info
        return info
    }
}
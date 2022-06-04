package org.crystal.intellij.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.psi.PsiElement
import org.crystal.intellij.psi.CrNamedElement
import org.crystal.intellij.psi.CrRecursiveVisitor

abstract class CrystalHighlightingVisitorBase(
    private val highlightInfos: MutableList<HighlightInfo>
) : CrRecursiveVisitor() {
    protected val CrNamedElement.defaultAnchor: PsiElement
        get() = nameElement ?: firstChild

    protected inline fun errorIf(anchor: PsiElement, description: String, validator: (String) -> String?) {
        error(anchor, validator(description) ?: return)
    }

    protected fun error(anchor: PsiElement, message: String) {
        val info = HighlightInfo
            .newHighlightInfo(HighlightInfoType.ERROR)
            .range(anchor)
            .descriptionAndTooltip(message)
            .create() ?: return
        highlightInfos += info
    }
}
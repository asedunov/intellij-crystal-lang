package org.crystal.intellij.quickFixes

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.siblings

fun HighlightInfo.Builder.withFix(action: IntentionAction?): HighlightInfo.Builder {
    if (action == null) return this
    return registerFix(action, null, null, null, null)
}

fun PsiElement.deleteWithLeadingSpaces(spaceAnchor: PsiElement = this) {
    val parent = parent
    val sibling = spaceAnchor.siblings(forward = false, withSelf = false).firstOrNull { it !is PsiWhiteSpace }
    if (sibling != null) {
        parent.deleteChildRange(sibling.nextSibling, this)
    }
    else {
        parent.deleteChildRange(parent.firstChild, this)
    }
}

fun PsiElement.deleteWithTrailingSpaces(spaceAnchor: PsiElement = this) {
    val parent = parent
    val sibling = spaceAnchor.siblings(withSelf = false).firstOrNull { it !is PsiWhiteSpace }
    if (sibling != null) {
        parent.deleteChildRange(this, sibling.prevSibling)
    } else {
        parent.deleteChildRange(this, parent.lastChild)
    }
}
package org.crystal.intellij.ide.quickFixes

import com.intellij.codeInsight.intention.FileModifier
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.startOffset
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.util.hasWhitespaceAt

class CrystalAddSpaceAction(
    element: PsiElement,
    @FileModifier.SafeFieldForPreview
    private val rangeInElement: TextRange
) : LocalQuickFixAndIntentionActionOnPsiElement(element) {
    override fun getFamilyName() = CrystalBundle.message("intention.add.leading.space.family")

    override fun getText() = familyName

    override fun invoke(project: Project, file: PsiFile, editor: Editor?, start: PsiElement, end: PsiElement) {
        val document = file.viewProvider.document ?: return
        val range = rangeInElement.shiftRight(start.startOffset)
        if (!document.hasWhitespaceAt(range.endOffset)) {
            document.insertString(range.endOffset, " ")
        }
        if (!document.hasWhitespaceAt(range.startOffset - 1)) {
            document.insertString(range.startOffset, " ")
        }
    }
}
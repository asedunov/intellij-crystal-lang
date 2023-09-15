package org.crystal.intellij.ide.quickFixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.lang.psi.CrHeredocLiteralBody
import org.crystal.intellij.lang.psi.CrHeredocRawElement
import org.crystal.intellij.lang.psi.childrenOfType
import org.crystal.intellij.lang.psi.lineRangesWithWrongIndent
import org.crystal.intellij.util.countLeadingSpaces

class CrystalFixHeredocIndentsAction(
    element: CrHeredocLiteralBody
) : LocalQuickFixAndIntentionActionOnPsiElement(element) {
    override fun getFamilyName() = CrystalBundle.message("quickfix.fix.heredoc.indents")

    override fun getText() = familyName

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        startElement: PsiElement,
        endElement: PsiElement
    ) {
        if (editor == null) return
        val doc = editor.document
        val chars = doc.charsSequence

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(doc)

        val body = startElement as? CrHeredocLiteralBody ?: return
        val indentSize = body.indentSize
        for (raw in body.childrenOfType<CrHeredocRawElement>().reversed()) {
            val ranges = raw.lineRangesWithWrongIndent().toList()
            for (i in ranges.lastIndex downTo 0) {
                val range = ranges[i]
                val offset = range.startOffset
                val spaces = chars.countLeadingSpaces(offset)
                if (spaces >= indentSize) continue
                doc.insertString(offset, " ".repeat(indentSize - spaces))
            }
        }
    }
}
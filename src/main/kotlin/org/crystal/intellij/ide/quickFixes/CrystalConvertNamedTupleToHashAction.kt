package org.crystal.intellij.ide.quickFixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.DocumentUtil
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.lang.lexer.CR_COLON
import org.crystal.intellij.lang.psi.CrNamedTupleExpression
import org.crystal.intellij.lang.psi.CrStringLiteralExpression
import org.crystal.intellij.lang.psi.firstChildWithElementType
import org.crystal.intellij.util.hasWhitespaceAt

class CrystalConvertNamedTupleToHashAction(
    literal: CrNamedTupleExpression
) : LocalQuickFixAndIntentionActionOnPsiElement(literal) {
    override fun getFamilyName() = CrystalBundle.message("intention.convert.named.tuple.to.hash.family")

    override fun getText() = familyName

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        startElement: PsiElement,
        endElement: PsiElement
    ) {
        val tuple = startElement as CrNamedTupleExpression
        val document = file.viewProvider.document
        DocumentUtil.executeInBulk(document) {
            for (entry in tuple.entries.reversed()) {
                val nameBody = entry.nameElement?.body
                val colon = entry.firstChildWithElementType(CR_COLON) ?: continue
                val spaceBefore = document.hasWhitespaceAt(colon.startOffset - 1)
                val spaceAfter = document.hasWhitespaceAt(colon.endOffset)
                val op = buildString {
                    if (!spaceBefore) append(" ")
                    append("=>")
                    if (!spaceAfter) append(" ")
                }
                document.replaceString(colon.startOffset, colon.endOffset, op)
                if (nameBody != null && nameBody !is CrStringLiteralExpression) {
                    document.replaceString(nameBody.startOffset, nameBody.endOffset, "\"${entry.name}\"")
                }
            }
        }
    }
}
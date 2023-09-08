package org.crystal.intellij.ide.editor

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.util.DocumentUtil

// Based on the com.intellij.codeInsight.editorActions.BackspaceHandler
class CrystalBackspaceHandler : BackspaceHandlerDelegate() {
    private var offset = 0
    private var wasClosingQuote = false

    override fun beforeCharDeleted(c: Char, file: PsiFile, editor: Editor) {
        offset = DocumentUtil.getPreviousCodePointOffset(editor.document, editor.caretModel.offset)
        wasClosingQuote = offset >= 0 && isClosingQuoteOrBracket(file, offset)
    }

    override fun charDeleted(c: Char, file: PsiFile, editor: Editor): Boolean {
        val settings = CodeInsightSettings.getInstance()
        val chars = editor.document.charsSequence

        if (c.isOpenBracket && settings.AUTOINSERT_PAIR_BRACKET) {
            if (chars.getOrNull(offset) == delimiters[c]) {
                editor.document.deleteString(offset, offset + 1)
            }
            return true
        }

        if (c.isQuote && settings.AUTOINSERT_PAIR_QUOTE) {
            if (chars.getOrNull(offset) == c && !wasClosingQuote) {
                editor.document.deleteString(offset, offset + 1)
            }
            return true
        }

        return false
    }
}
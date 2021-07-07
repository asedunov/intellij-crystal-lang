package org.crystal.intellij.editor

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.injected.editor.EditorWindow
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.crystal.intellij.psi.CrFile

// Based on the com.intellij.codeInsight.editorActions.SelectionQuotingTypedHandler
class CrystalTypedHandler : TypedHandlerDelegate() {
    override fun beforeSelectionRemoved(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file !is CrFile) return Result.CONTINUE

        if (!c.isQuote) return Result.CONTINUE
        if (!CodeInsightSettings.getInstance().SURROUND_SELECTION_ON_QUOTE_TYPED) return Result.CONTINUE
        val selectionModel = editor.selectionModel
        if (!selectionModel.hasSelection()) return Result.CONTINUE
        var selectedText = selectionModel.selectedText
        if (selectedText.isNullOrEmpty()) return Result.CONTINUE

        val selectionStart = selectionModel.selectionStart
        val selectionEnd = selectionModel.selectionEnd

        if (selectedText.length > 1) {
            val firstChar = selectedText.first()
            val lastChar = selectedText.last()
            if (firstChar.isQuote && lastChar == firstChar && !selectedText.containsQuoteInside(lastChar)) {
                selectedText = selectedText.substring(1, selectedText.length - 1)
            }
        }
        val caretOffset = selectionModel.selectionStart
        val newText = "$c$selectedText$c"
        val ltrSelection = selectionModel.leadSelectionOffset != selectionModel.selectionEnd
        val restoreStickySelection = editor is EditorEx && editor.isStickySelection
        selectionModel.removeSelection()
        editor.document.replaceString(selectionStart, selectionEnd, newText)

        val startOffset = caretOffset + 1
        val endOffset = caretOffset + newText.length - 1
        val length = editor.document.textLength

        // selection is removed here
        if (endOffset <= length) {
            if (restoreStickySelection) {
                val editorEx = editor as EditorEx
                val caretModel = editorEx.caretModel
                caretModel.moveToOffset(if (ltrSelection) startOffset else endOffset)
                editorEx.isStickySelection = true
                caretModel.moveToOffset(if (ltrSelection) endOffset else startOffset)
            } else {
                if (ltrSelection || editor is EditorWindow) {
                    editor.selectionModel.setSelection(startOffset, endOffset)
                } else {
                    editor.selectionModel.setSelection(endOffset, startOffset)
                }
                editor.caretModel.moveToOffset(if (ltrSelection) endOffset else startOffset)
            }
        }

        return Result.STOP
    }

    private val Char.isQuote
        get() = this in quotes

    private fun String.containsQuoteInside(quote: Char) = indexOf(quote, 1) != lastIndex
}

val quotes = setOf('"', '\'', '`', '/')
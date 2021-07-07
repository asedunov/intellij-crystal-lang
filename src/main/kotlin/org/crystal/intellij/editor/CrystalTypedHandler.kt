package org.crystal.intellij.editor

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.injected.editor.EditorWindow
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.crystal.intellij.psi.CrFile
import org.crystal.intellij.psi.parents

// Based on the com.intellij.codeInsight.editorActions.SelectionQuotingTypedHandler
class CrystalTypedHandler : TypedHandlerDelegate() {
    override fun beforeSelectionRemoved(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file !is CrFile) return Result.CONTINUE

        if (!c.isQuote) return Result.CONTINUE

        val selectionModel = editor.selectionModel
        if (!selectionModel.hasSelection()) return Result.CONTINUE

        var selectedText = selectionModel.selectedText
        if (selectedText.isNullOrEmpty()) return Result.CONTINUE

        if (CodeInsightSettings.getInstance().AUTOINSERT_PAIR_QUOTE && selectedText.length == 1) {
            val selectedChar = selectedText.first()
            if (selectedChar.isQuote &&
                selectedChar != c &&
                replaceQuotesBySelected(c, editor, file, selectionModel, selectedChar)) return Result.STOP
        }

        if (CodeInsightSettings.getInstance().SURROUND_SELECTION_ON_QUOTE_TYPED) {
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

        return Result.CONTINUE
    }

    private val Char.isQuote
        get() = this in quotes

    private fun String.containsQuoteInside(quote: Char) = indexOf(quote, 1) != lastIndex

    private fun replaceQuotesBySelected(
        c: Char,
        editor: Editor,
        file: PsiFile,
        selectionModel: SelectionModel,
        selectedChar: Char
    ): Boolean {
        val selectionStart = selectionModel.selectionStart
        val document = editor.document
        val charsSequence = document.charsSequence
        file.findElementAt(selectionStart)?.parents()?.forEach { element ->
            val range = element.textRange
            if (range == null || range.length < 2) return@forEach

            val isAtStart = range.startOffset == selectionStart
            if (isAtStart || range.endOffset == selectionStart + 1 && c.isQuote) {
                val matchingCharOffset = if (isAtStart) range.endOffset - 1 else range.startOffset
                if (matchingCharOffset < charsSequence.length) {
                    val matchingChar = charsSequence[matchingCharOffset]
                    if (matchingChar == selectedChar && !document.getText(range).containsQuoteInside(charsSequence[range.endOffset - 1])) {
                        document[range.startOffset] = c
                        document[range.endOffset - 1] = c
                        editor.caretModel.moveToOffset(selectionModel.selectionEnd)
                        selectionModel.removeSelection()
                        return true
                    }
                }
            }
        }
        return false
    }

    private operator fun Document.set(offset: Int, newChar: Char) {
        replaceString(offset, offset + 1, newChar.toString())
    }
}

val quotes = setOf('"', '\'', '`', '/')
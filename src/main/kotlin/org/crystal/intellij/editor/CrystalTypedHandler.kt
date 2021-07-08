package org.crystal.intellij.editor

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.injected.editor.EditorWindow
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.impl.UndoManagerImpl
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_CHAR_END
import org.crystal.intellij.lexer.CR_COMMAND_END
import org.crystal.intellij.lexer.CR_REGEX_END
import org.crystal.intellij.lexer.CR_STRING_END
import org.crystal.intellij.psi.*

class CrystalTypedHandler : TypedHandlerDelegate() {
    override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
        if (file !is CrFile) return Result.CONTINUE

        // Based on the com.intellij.codeInsight.editorActions.TypedHandler.handleQuote
        if (CodeInsightSettings.getInstance().AUTOINSERT_PAIR_QUOTE && c.isQuote) {
            val offset = editor.caretModel.offset
            val document = editor.document
            val chars = document.charsSequence
            val length = document.textLength

            if (offset < length && chars[offset] == c) {
                if (isClosingQuote(file, offset)) {
                    EditorModificationUtil.moveCaretRelatively(editor, 1)
                    return Result.STOP
                }
            }

            if (c != '/' && !isInsideLiteral(file, offset)) {
                type(editor, project, "$c$c")
                EditorModificationUtil.moveCaretRelatively(editor, -1)
                return Result.STOP
            }
        }

        return Result.CONTINUE
    }

    // Based on the com.intellij.codeInsight.editorActions.SelectionQuotingTypedHandler
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

    private fun isInsideLiteral(file: PsiFile, offset: Int): Boolean {
        val parent = file.findElementAt(offset)?.parent
        return parent is CrStringLiteralExpression ||
                parent is CrCharLiteralExpression ||
                parent is CrCommandExpression ||
                parent is CrRegexExpression ||
                parent is CrSymbolArrayExpression ||
                parent is CrStringArrayExpression ||
                parent is CrSymbolExpression
    }

    private fun isClosingQuote(file: PsiFile, offset: Int): Boolean {
        return when (file.findElementAt(offset)?.elementType) {
            CR_CHAR_END, CR_COMMAND_END, CR_REGEX_END, CR_STRING_END -> true
            else -> false
        }
    }

    private fun type(editor: Editor, project: Project, text: String) {
        CommandProcessor.getInstance().currentCommandName = EditorBundle.message("typing.in.editor.command.name")
        EditorModificationUtil.insertStringAtCaret(editor, text, true, true)
        (UndoManager.getInstance(project) as UndoManagerImpl).addDocumentAsAffected(editor.document)
    }
}

val quotes = setOf('"', '\'', '`', '/')
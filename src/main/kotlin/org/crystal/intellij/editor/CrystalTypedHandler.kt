package org.crystal.intellij.editor

import com.google.common.collect.ImmutableBiMap
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
import org.crystal.intellij.psi.*

class CrystalTypedHandler : TypedHandlerDelegate() {
    override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
        if (file !is CrFile) return Result.CONTINUE

        // Based on the com.intellij.codeInsight.editorActions.TypedHandler.handleQuote

        val settings = CodeInsightSettings.getInstance()

        val c1: Char
        val c2: Char
        when {
            c.isQuote -> {
                if (!settings.AUTOINSERT_PAIR_QUOTE) return Result.CONTINUE
                c1 = c
                c2 = c
            }

            c.isOpenBracket -> {
                if (!settings.AUTOINSERT_PAIR_BRACKET) return Result.CONTINUE
                c1 = c
                c2 = delimiters[c]!!
            }

            c.isCloseBracket -> {
                if (!settings.AUTOINSERT_PAIR_BRACKET) return Result.CONTINUE
                c1 = delimiters.inverse()[c]!!
                c2 = c
            }

            else -> return Result.CONTINUE
        }

        val offset = editor.caretModel.offset
        val document = editor.document
        val chars = document.charsSequence
        val length = document.textLength

        if (offset < length && chars[offset] == c && c == c2) {
            if (isClosingQuoteOrBracket(file, offset)) {
                EditorModificationUtil.moveCaretRelatively(editor, 1)
                return Result.STOP
            }
        }

        if (c == c1 && c != '/' && !isInsideLiteral(file, offset)) {
            type(editor, project, "$c$c2")
            EditorModificationUtil.moveCaretRelatively(editor, -1)
            return Result.STOP
        }

        return Result.CONTINUE
    }

    // Based on the com.intellij.codeInsight.editorActions.SelectionQuotingTypedHandler
    override fun beforeSelectionRemoved(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file !is CrFile) return Result.CONTINUE

        val c2 = delimiters[c] ?: return Result.CONTINUE

        val selectionModel = editor.selectionModel
        if (!selectionModel.hasSelection()) return Result.CONTINUE

        var selectedText = selectionModel.selectedText
        if (selectedText.isNullOrEmpty()) return Result.CONTINUE

        val settings = CodeInsightSettings.getInstance()

        if (settings.AUTOINSERT_PAIR_QUOTE && selectedText.length == 1) {
            val selectedChar = selectedText.first()
            if (selectedChar.isSimilarDelimiterTo(c) &&
                selectedChar != c &&
                replaceQuotesBySelected(c, editor, file, selectionModel, selectedChar)) return Result.STOP
        }

        if (settings.SURROUND_SELECTION_ON_QUOTE_TYPED) {
            val selectionStart = selectionModel.selectionStart
            val selectionEnd = selectionModel.selectionEnd

            if (selectedText.length > 1) {
                val firstChar = selectedText.first()
                val lastChar = selectedText.last()
                if (firstChar.isSimilarDelimiterTo(c) &&
                    lastChar == delimiters[firstChar] &&
                    (firstChar.isQuote || firstChar != c) &&
                    !selectedText.containsQuoteInside(lastChar)) {
                    selectedText = selectedText.substring(1, selectedText.length - 1)
                }
            }
            val caretOffset = selectionModel.selectionStart
            val newText = "$c$selectedText$c2"
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

    private val Char.isOpenBracket
        get() = brackets.containsKey(this)

    private val Char.isCloseBracket
        get() = brackets.containsValue(this)

    private val Char.isBracket
        get() = isOpenBracket || isCloseBracket

    private fun Char.isSimilarDelimiterTo(ch: Char) =
        isBracket && ch.isBracket || isQuote && ch.isQuote

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
                    val otherQuoteMatchesSelected = if (isAtStart) {
                        matchingChar == delimiters[selectedChar]
                    } else {
                        selectedChar == delimiters[matchingChar]
                    }
                    if (otherQuoteMatchesSelected && !document.getText(range).containsQuoteInside(charsSequence[range.endOffset - 1])) {
                        document[range.startOffset] = c
                        document[range.endOffset - 1] = delimiters[c]!!
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

    private fun type(editor: Editor, project: Project, text: String) {
        CommandProcessor.getInstance().currentCommandName = EditorBundle.message("typing.in.editor.command.name")
        EditorModificationUtil.insertStringAtCaret(editor, text, true, true)
        (UndoManager.getInstance(project) as UndoManagerImpl).addDocumentAsAffected(editor.document)
    }
}

val quotes = setOf('"', '\'', '`', '/')
val brackets = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>', '|' to '|')
val delimiters = ImmutableBiMap.builder<Char, Char>()
    .apply {
        for (quote in quotes) put(quote, quote)
        for ((lBracket, rBracket) in brackets) put(lBracket, rBracket)
    }
    .build()!!
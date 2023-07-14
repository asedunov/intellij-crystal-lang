package org.crystal.intellij.editor

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate.Result.*
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.suggested.startOffset
import org.crystal.intellij.lexer.*
import org.crystal.intellij.psi.*

class CrystalEnterHandler : EnterHandlerDelegateAdapter() {
    override fun preprocessEnter(
        file: PsiFile,
        editor: Editor,
        caretOffset: Ref<Int>,
        caretAdvance: Ref<Int>,
        dataContext: DataContext,
        originalHandler: EditorActionHandler?
    ): EnterHandlerDelegate.Result {
        val document = editor.document
        val project = dataContext.getData(CommonDataKeys.PROJECT) ?: return Continue
        if (file !is CrFile || editor.isViewer || !document.isWritable) return Continue

        PsiDocumentManager.getInstance(project).commitDocument(document)

        val settings = CodeInsightSettings.getInstance()

        val caretModel = editor.caretModel
        val offset = caretModel.offset
        if (offset == 0) return Continue
        val lineNumber = document.getLineNumber(offset)
        val element = file.findElementAt(offset - 1) ?: return Default

        val endHolder = getUnterminatedEndHolder(element, settings)
        if (endHolder != null) {
            val lineEndOffset = document.getLineEndOffset(lineNumber)
            if (lineEndOffset != offset) caretModel.moveToOffset(lineEndOffset)
            var indentAnchor = endHolder
            if (endHolder is CrBlockExpression) {
                val type = endHolder.firstChild.elementType
                if (type == CR_DO || type == CR_LBRACE) {
                    indentAnchor = indentAnchor.parentOfType<CrCallExpression>() ?: indentAnchor
                }
            }
            val startOffset = indentAnchor.startOffset
            val startLine = document.getLineNumber(startOffset)
            val indentSize = startOffset - document.getLineStartOffset(startLine)
            val needExtraLine = endHolder !is CrAnnotation
            val terminator =
                if (endHolder is CrBlockExpression && endHolder.firstChild.elementType == CR_LBRACE) "}" else "end"
            EditorModificationUtil.insertStringAtCaret(editor, "\n${" ".repeat(indentSize)}$terminator")
            if (lineEndOffset != offset) caretModel.moveToOffset(offset)
            return if (needExtraLine) Continue else Stop
        }

        return super.preprocessEnter(file, editor, caretOffset, caretAdvance, dataContext, originalHandler)
    }

    private fun getUnterminatedEndHolder(element: PsiElement, settings: CodeInsightSettings): PsiElement? {
        if (!settings.SMART_END_ACTION) return null

        var holder: PsiElement? = null
        for (p in element.parents()) {
            val needsEnd = when (p) {
                is CrFunction -> p.parentOfType<CrDefinition>() !is CrLibrary
                is CrAnnotation,
                is CrModuleLikeDefinition<*, *>,
                is CrFunctionLikeDefinition,
                is CrWhileExpression,
                is CrUntilExpression,
                is CrCaseExpression,
                is CrSelectExpression -> true
                is CrIfExpression,
                is CrUnlessExpression -> p.firstChild.elementType.let { it is CrystalKeywordTokenType && it != CR_ELSIF }
                is CrBlockExpression -> {
                    val type = p.firstChild.elementType
                    type == CR_DO || type == CR_LBRACE || type == CR_BEGIN
                }
                else -> false
            }
            if (needsEnd) {
                if (holder == null) holder = p
                val endType = if (p is CrBlockExpression && p.firstChild.elementType == CR_LBRACE) CR_RBRACE else CR_END
                if (p.lastChild.elementType != endType) return holder
            }
        }

        return null
    }
}
package org.crystal.intellij.completion

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.openapi.editor.EditorModificationUtil

fun InsertionContext.addSuffix(suffix: String, pos: Int = suffix.length) {
    document.insertString(selectionEndOffset, suffix)
    EditorModificationUtil.moveCaretRelatively(editor, pos)
}
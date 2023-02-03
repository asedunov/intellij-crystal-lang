package org.crystal.intellij.util

import com.intellij.openapi.editor.Document

fun Document.hasWhitespaceAt(index: Int): Boolean {
    return charsSequence.getOrNull(index)?.isWhitespace() == true
}
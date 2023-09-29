package org.crystal.intellij.lang.ast.location

import com.intellij.psi.PsiFile
import org.crystal.intellij.lang.psi.crColumnNumber
import org.crystal.intellij.lang.psi.crLineNumber

data class CstLocation(
    val startOffset: Int,
    val endOffset: Int,
    val file: PsiFile
) {
    val startLine: Int
        get() = file.crLineNumber(startOffset)

    val startColumn: Int
        get() = file.crColumnNumber(startOffset)

    val endLine: Int
        get() = file.crLineNumber(endOffset - 1)

    val endColumn: Int
        get() = file.crColumnNumber(endOffset - 1)
}
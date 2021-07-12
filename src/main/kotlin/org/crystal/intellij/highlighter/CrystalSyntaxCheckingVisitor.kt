package org.crystal.intellij.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.psi.PsiElement
import org.crystal.intellij.psi.*

class CrystalSyntaxCheckingVisitor(
    private val highlightInfos: MutableList<HighlightInfo>
) : CrRecursiveVisitor() {
    override fun visitIntegerLiteralExpression(o: CrIntegerLiteralExpression) {
        super.visitIntegerLiteralExpression(o)

        if (o.value == null) {
            error(o, "The value is out of ${o.kind.typeName} range")
        }

        if (o.prefix == "0") {
            error(o, "Octal constants should be prefixed with 0o")
        }
    }

    override fun visitOctalEscapeElement(o: CrOctalEscapeElement) {
        if (o.escapedChar > maxOctalChar) {
            error(o, "Octal value may not exceed 377 (decimal 256)")
        }
    }

    override fun visitHexEscapeElement(o: CrHexEscapeElement) {
        if (o.escapedChar == null) {
            error(o, "Invalid hex escape")
        }
    }

    override fun visitUnicodeEscapeElement(o: CrUnicodeEscapeElement) {
        handleUnicode(o)
    }

    override fun visitCharCodeElement(o: CrCharCodeElement) {
        handleUnicode(o)
    }

    private fun handleUnicode(o: CrCharEscapeHolder) {
        val message = when (o.escapedChar) {
            null -> "Invalid Unicode codepoint"
            in Char.MIN_SURROGATE..Char.MAX_SURROGATE -> "Invalid Unicode codepoint (surrogate half)"
            else -> return
        }
        error(o, message)
    }

    private fun error(anchor: PsiElement, message: String) {
        val info = HighlightInfo
            .newHighlightInfo(HighlightInfoType.ERROR)
            .range(anchor)
            .descriptionAndTooltip(message)
            .create() ?: return
        highlightInfos += info
    }

    companion object {
        private const val maxOctalChar = 255.toChar()
    }
}
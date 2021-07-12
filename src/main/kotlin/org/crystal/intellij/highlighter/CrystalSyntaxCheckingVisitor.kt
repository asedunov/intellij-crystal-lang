package org.crystal.intellij.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.psi.PsiElement
import org.crystal.intellij.psi.CrHexEscapeElement
import org.crystal.intellij.psi.CrIntegerLiteralExpression
import org.crystal.intellij.psi.CrOctalEscapeElement
import org.crystal.intellij.psi.CrRecursiveVisitor

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
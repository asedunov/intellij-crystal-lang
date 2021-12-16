package org.crystal.intellij.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import org.crystal.intellij.editor.delimiters

class CrStringLiteralManipulator : AbstractElementManipulator<CrStringLiteralExpression>() {
    override fun handleContentChange(
        element: CrStringLiteralExpression,
        range: TextRange,
        newContent: String?
    ): CrStringLiteralExpression {
        val newLiteralText = buildLiteralText(element, range, newContent ?: "")
        val newLiteral = CrPsiFactory
            .getInstance(element.project)
            .createExpression<CrStringLiteralExpression>(newLiteralText)
        return element.replaceTyped(newLiteral)
    }

    override fun getRangeInElement(element: CrStringLiteralExpression) = element.valueRangeInElement

    private fun buildLiteralText(
        element: CrStringLiteralExpression,
        range: TextRange,
        newContent: String
    ): String {
        val openQuote = element.openQuote.text

        if (openQuote == "\"") {
            return combineTextAsIs(element, range, newContent.escape('\\', '"'))
        }

        if ("%" in openQuote) {
            val forbidEscapes = openQuote.startsWith("%q")
            val lBracket = openQuote.last()
            if (lBracket == '|') {
                if (forbidEscapes) {
                    return if ('|' in newContent) {
                        combineTextWithLiteralConversion(element, range, newContent, lBracket, lBracket)
                    }
                    else {
                        combineTextAsIs(element, range, newContent)
                    }
                }
                return combineTextAsIs(element, range, newContent.escape('\\', '|'))
            }
            val rBracket = delimiters[lBracket]!!
            val oldContent = buildString {
                element.createLiteralTextEscaper().decode(range, this)
            }
            val oldBalance = oldContent.countBalance(lBracket, rBracket)
            val newBalance = newContent.countBalance(lBracket, rBracket)
            return if (oldBalance == newBalance) {
                combineTextAsIs(element, range, if (forbidEscapes) newContent else newContent.escape('\\'))
            } else if (forbidEscapes) {
                combineTextWithLiteralConversion(element, range, newContent, lBracket, rBracket)
            }
            else {
                combineTextWithBracketEscaping(element, range, newContent, lBracket, rBracket)
            }
        }

        return combineTextAsIs(element, range, newContent)
    }

    private fun String.countBalance(lBracket: Char, rBracket: Char): Int {
        var balance = 0
        for (c in this) {
            when (c) {
                lBracket -> balance++
                rBracket -> balance--
            }
        }
        return balance
    }

    private fun String.escape(
        vararg charsToEscape: Char,
    ) = escape(0, length, *charsToEscape)

    private fun String.escape(
        from: Int,
        to: Int,
        vararg charsToEscape: Char,
    ) = buildString {
        escapeTo(this, from, to, *charsToEscape)
    }

    private fun String.escapeTo(
        result: StringBuilder,
        vararg charsToEscape: Char,
    ) {
        escapeTo(result, 0, length, *charsToEscape)
    }

    private fun String.escapeTo(
        result: StringBuilder,
        from: Int,
        to: Int,
        vararg charsToEscape: Char,
    ) {
        for (i in from until to) {
            val c = this[i]
            if (c in charsToEscape) {
                result.append('\\')
            }
            result.append(c)
        }
    }

    private fun String.escapeExtraTo(
        result: StringBuilder,
        from: Int = 0,
        to: Int = length,
        vararg charsToEscape: Char
    ) {
        for (i in from until to) {
            val c = this[i]
            if (c in charsToEscape && (i == 0 || this[i - 1] != '\\')) {
                result.append('\\')
            }
            result.append(c)
        }
    }

    private fun combineTextAsIs(
        element: CrStringLiteralExpression,
        range: TextRange,
        newEscapedContent: String
    ): String {
        val oldText = element.text
        return oldText.substring(0, range.startOffset) +
                newEscapedContent +
                oldText.substring(range.endOffset)
    }

    private fun combineTextWithBracketEscaping(
        element: CrStringLiteralExpression,
        range: TextRange,
        newContent: String,
        lBracket: Char,
        rBracket: Char
    ): String {
        val oldText = element.text
        val valueRange = element.valueRangeInElement
        return buildString {
            append(oldText, 0, valueRange.startOffset)
            oldText.escapeExtraTo(this, valueRange.startOffset, range.startOffset, lBracket, rBracket)
            newContent.escapeTo(this, '\\', lBracket, rBracket)
            oldText.escapeExtraTo(this, range.endOffset, valueRange.endOffset, lBracket, rBracket)
            append(oldText, valueRange.endOffset, oldText.length)
        }
    }

    private fun combineTextWithLiteralConversion(
        element: CrStringLiteralExpression,
        range: TextRange,
        newContent: String,
        lBracket: Char,
        rBracket: Char
    ): String {
        val oldText = element.text
        val valueRange = element.valueRangeInElement
        val charsToEscape = if (lBracket != rBracket) charArrayOf('\\', lBracket, rBracket) else charArrayOf('\\', lBracket)
        return buildString {
            append('%').append(lBracket)
            oldText.escapeTo(this, valueRange.startOffset, range.startOffset, *charsToEscape)
            newContent.escapeTo(this, *charsToEscape)
            oldText.escapeTo(this, range.endOffset, valueRange.endOffset, *charsToEscape)
            append(rBracket)
        }
    }
}
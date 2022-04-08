package org.crystal.intellij.tests

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.editor.brackets
import org.crystal.intellij.editor.quotes
import org.junit.Test

class CrystalTypeHandlerTest : BasePlatformTestCase() {
    private fun doTest(before: String, ch: Char, after: String) {
        myFixture.configureByText("a.cr", before)
        myFixture.type(ch)
        myFixture.checkResult(after)
    }

    private val percentKinds = listOf("", "q", "Q", "i", "r", "w", "x")

    @Test
    fun testAutoSurroundForQuotes() {
        for (quoteBefore in quotes) {
            for (quoteAfter in quotes) {
                if (quoteBefore == quoteAfter) continue

                doTest(
                    "<selection><caret>${quoteBefore}foo${quoteBefore}</selection>",
                    quoteAfter,
                    "${quoteAfter}<selection><caret>foo</selection>${quoteAfter}"
                )
            }
        }
    }

    @Test
    fun testAutoSurroundForBrackets() {
        for ((leftBefore, rightBefore) in brackets) {
            for ((leftAfter, rightAfter) in brackets) {
                if (leftBefore == leftAfter) continue

                doTest(
                    "<selection><caret>${leftBefore}foo${rightBefore}</selection>",
                    leftAfter,
                    "${leftAfter}<selection><caret>foo</selection>${rightAfter}"
                )
            }
        }
    }

    @Test
    fun testPairedQuoteReplacement() {
        for (quoteBefore in quotes) {
            for (quoteAfter in quotes) {
                if (quoteBefore == quoteAfter) continue

                doTest(
                    "<selection>${quoteBefore}</selection>foo${quoteBefore}",
                    quoteAfter,
                    "${quoteAfter}<caret>foo${quoteAfter}"
                )
            }
        }
    }

    @Test
    fun testPairedBracketReplacement() {
        for ((leftBefore, rightBefore) in brackets) {
            for ((leftAfter, rightAfter) in brackets) {
                if (leftBefore == leftAfter) continue

                doTest(
                    "<selection>${leftBefore}</selection>foo${rightBefore}",
                    leftAfter,
                    "${leftAfter}<caret>foo${rightAfter}"
                )
            }
        }
    }

    @Test
    fun testInsertPairQuoteOutsideString() {
        for (quote in quotes - '/') {
            doTest(
                "x = <caret>",
                quote,
                "x = $quote<caret>$quote"
            )
        }
    }

    @Test
    fun testInsertPairBracketOutsideString() {
        for ((left, right) in brackets) {
            val shouldPair = !(left == '<' || left == '|')
            doTest(
                "x = <caret>",
                left,
                if (shouldPair) "x = $left<caret>$right" else "x = $left<caret>"
            )
        }
    }

    @Test
    fun testInsertPairBracketOutsideStringAfterPercent() {
        for ((left, right) in brackets) {
            doTest(
                "x = %<caret>",
                left,
                "x = %$left<caret>$right"
            )
        }
    }

    @Test
    fun testInsertPairQuoteInsideSimpleString() {
        for (quote in quotes) {
            for (stringQuote in quotes) {
                doTest(
                    "x = $stringQuote<caret>a$stringQuote",
                    quote,
                    "x = $stringQuote$quote<caret>a$stringQuote"
                )
            }
        }
    }

    @Test
    fun testInsertPairBracketInsideSimpleString() {
        for (left in brackets.keys) {
            for (stringQuote in quotes) {
                doTest(
                    "x = $stringQuote<caret>a$stringQuote",
                    left,
                    "x = $stringQuote$left<caret>a$stringQuote"
                )
            }
        }
    }

    @Test
    fun testInsertPairQuoteInsideQuotedSymbol() {
        for (quote in quotes) {
            doTest(
                "x = :\"<caret>a\"",
                quote,
                "x = :\"$quote<caret>a\""
            )
        }
    }

    @Test
    fun testInsertPairBracketInsideQuotedSymbol() {
        for (left in brackets.keys) {
            doTest(
                "x = :\"<caret>a\"",
                left,
                "x = :\"$left<caret>a\""
            )
        }
    }

    @Test
    fun testInsertPairQuoteInsidePercentString() {
        for (quote in quotes) {
            for (kind in percentKinds) {
                for ((lBracket, rBracket) in brackets) {
                    val left = "%$kind$lBracket"
                    val right = "%$kind$rBracket"
                    doTest(
                        "x = $left<caret>a$right",
                        quote,
                        "x = $left$quote<caret>a$right"
                    )
                }
            }
        }
    }

    @Test
    fun testInsertPairBracketInsidePercentString() {
        for (typedLeft in brackets.keys) {
            for (kind in percentKinds) {
                for ((lBracket, rBracket) in brackets) {
                    val left = "%$kind$lBracket"
                    val right = "%$kind$rBracket"
                    doTest(
                        "x = $left<caret>a$right",
                        typedLeft,
                        "x = $left$typedLeft<caret>a$right"
                    )
                }
            }
        }
    }

    @Test
    fun testNoInsertBeforeEndQuote() {
        for (quote in quotes) {
            doTest(
                "x = ${quote}a<caret>${quote}",
                quote,
                "x = ${quote}a${quote}<caret>"
            )
        }
    }

    @Test
    fun testNoInsertBeforeEndBracket() {
        for ((left, right) in brackets) {
            doTest(
                "x = ${left}a<caret>${right}",
                right,
                "x = ${left}a${right}<caret>"
            )
        }
    }
}
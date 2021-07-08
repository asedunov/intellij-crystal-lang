package org.crystal.intellij.tests

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.editor.quotes

class CrystalTypeHandlerTest : BasePlatformTestCase() {
    private fun doTest(before: String, ch: Char, after: String) {
        myFixture.configureByText("a.cr", before)
        myFixture.type(ch)
        myFixture.checkResult(after)
    }

    private val bracketMap = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '<', '|' to '|')

    private val percentKinds = listOf("", "q", "Q", "i", "r", "w", "x")

    fun testAutoSurroundForQuotes() {
        for (quoteBefore in quotes) {
            for (quoteAfter in quotes) {
                if (quoteBefore == quoteAfter) continue

                doTest(
                    "<selection>${quoteBefore}foo${quoteBefore}</selection>",
                    quoteAfter,
                    "${quoteAfter}<selection><caret>foo</selection>${quoteAfter}"
                )
            }
        }
    }

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

    fun testInsertPairQuoteOutsideString() {
        for (quote in quotes - '/') {
            doTest(
                "x = <caret>",
                quote,
                "x = $quote<caret>$quote"
            )
        }
    }

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

    fun testInsertPairQuoteInsideQuotedSymbol() {
        for (quote in quotes) {
            doTest(
                "x = :\"<caret>a\"",
                quote,
                "x = :\"$quote<caret>a\""
            )
        }
    }

    fun testInsertPairQuoteInsidePercentString() {
        for (quote in quotes) {
            for (kind in percentKinds) {
                for ((lBracket, rBracket) in bracketMap) {
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

    fun testNoInsertBeforeEndQuote() {
        for (quote in quotes) {
            doTest(
                "x = ${quote}a<caret>${quote}",
                quote,
                "x = ${quote}a${quote}<caret>"
            )
        }
    }
}
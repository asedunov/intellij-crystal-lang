package org.crystal.intellij.tests

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.editor.quotes

class CrystalTypeHandlerTest : BasePlatformTestCase() {
    private fun doTest(before: String, ch: Char, after: String) {
        myFixture.configureByText("a.cr", before)
        myFixture.type(ch)
        myFixture.checkResult(after)
    }

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
}
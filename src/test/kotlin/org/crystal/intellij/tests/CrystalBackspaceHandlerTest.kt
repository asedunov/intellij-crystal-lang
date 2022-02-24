package org.crystal.intellij.tests

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.testFramework.LightPlatformCodeInsightTestCase
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.crystal.intellij.editor.brackets
import org.crystal.intellij.editor.quotes
import org.junit.Test

class CrystalBackspaceHandlerTest : BasePlatformTestCase() {
    private fun CodeInsightTestFixture.backspace() {
        LightPlatformCodeInsightTestCase.executeAction(IdeActions.ACTION_EDITOR_BACKSPACE, editor, project)
    }

    private fun doTest(before: String, after: String) {
        myFixture.configureByText("a.cr", before)
        myFixture.backspace()
        myFixture.checkResult(after)
    }

    @Test
    fun testAutoRemoveOpenQuote() {
        for (quote in quotes) {
            doTest(
                "1\n${quote}<caret>${quote}",
                "1\n<caret>"
            )
        }
    }

    @Test
    fun testAfterClosingQuote() {
        for (quote in quotes) {
            doTest(
                "1\n${quote}${quote}<caret>",
                "1\n${quote}<caret>"
            )
        }
    }

    @Test
    fun testAutoRemoveOpenBracket() {
        for ((left, right) in brackets) {
            doTest(
                "1\n${left}<caret>${right}",
                "1\n<caret>"
            )
        }
    }
}
package org.crystal.intellij.tests

import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.suggested.startOffset
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.psi.CrStringLiteralExpression
import org.junit.Assert
import org.junit.Test

class CrystalStringLiteralEscaperTest : BasePlatformTestCase() {
    private fun doTest(literalText: String, expectedValue: String, vararg expectedOffsets: Int) {
        myFixture.configureByText("a.cr", literalText)
        val selectionModel = myFixture.editor.selectionModel
        val selectionStart = selectionModel.selectionStart
        val selectionEnd = selectionModel.selectionEnd
        val literal = myFixture.file.findElementAt(selectionStart)!!.parentOfType<CrStringLiteralExpression>()!!
        val literalOffset = literal.startOffset
        val range = TextRange(selectionStart - literalOffset, selectionEnd - literalOffset)
        val escaper = literal.createLiteralTextEscaper()
        val actualValue = buildString {
            escaper.decode(range, this)
        }
        Assert.assertEquals(expectedValue, actualValue)
        val actualOffsets = IntArray(actualValue.length) { i -> escaper.getOffsetInHost(i, range) }
        Assert.assertArrayEquals(expectedOffsets, actualOffsets)
    }

    @Test
    fun testSingleRawFull() = doTest(
        "\"<selection>abcdef</selection>\"",
        "abcdef",
        1, 2, 3, 4, 5, 6
    )

    @Test
    fun testSingleRawPartial() = doTest(
        "\"a<selection>bcde</selection>f\"",
        "bcde",
        2, 3, 4, 5
    )

    @Test
    fun testMultiRawsFull() = doTest(
        "\"<selection>abc\\ndef</selection>\"",
        "abc\ndef",
        1, 2, 3, 4, 6, 7, 8
    )

    @Test
    fun testMultiRawsPartial() = doTest(
        "\"a<selection>bc\\nde</selection>f\"",
        "bc\nde",
        2, 3, 4, 6, 7
    )

    @Test
    fun testSpecialEscapeFull() = doTest(
        "\"<selection>\\n</selection>\\n\"",
        "\n",
        1
    )

    @Test
    fun testSpecialEscapeMulti() = doTest(
        "\"<selection>\\n\\n</selection>\"",
        "\n\n",
        1, 3
    )

    @Test
    fun testSpecialEscapePartial() = doTest(
        "\"\\<selection>n\\n</selection>\"",
        ""
    )

    @Test
    fun testRawEscapeFull() = doTest(
        "\"<selection>\\(</selection>\\)\"",
        "(",
        1
    )

    @Test
    fun testRawEscapeMulti() = doTest(
        "\"<selection>\\(\\)</selection>\"",
        "()",
        1, 3
    )

    @Test
    fun testRawEscapePartial() = doTest(
        "\"\\<selection>(\\)</selection>\"",
        ""
    )

    @Test
    fun testOctalEscapeFull() = doTest(
        "\"<selection>\\0123</selection>\\0123\"",
        "S",
        1
    )

    @Test
    fun testOctalEscapeMulti() = doTest(
        "\"<selection>\\0123\\0124</selection>\"",
        "ST",
        1, 6
    )

    @Test
    fun testOctalEscapePartial() = doTest(
        "\"\\01<selection>23\\0123</selection>\"",
        ""
    )

    @Test
    fun testHexEscapeFull() = doTest(
        "\"<selection>\\x53</selection>\\x54\"",
        "S",
        1
    )

    @Test
    fun testHexEscapeMulti() = doTest(
        "\"<selection>\\x53\\x54</selection>\"",
        "ST",
        1, 5
    )

    @Test
    fun testHexEscapePartial() = doTest(
        "\"\\x<selection>53\\x54</selection>\"",
        ""
    )

    @Test
    fun testCharCodeEscapeFull() = doTest(
        "\"<selection>\\u0053</selection>\\u0054\"",
        "S",
        1
    )

    @Test
    fun testCharCodeEscapeMulti() = doTest(
        "\"<selection>\\u0053\\u0054</selection>\"",
        "ST",
        1, 7
    )

    @Test
    fun testCharCodeEscapePartial() = doTest(
        "\"\\u00<selection>53\\u0054</selection>\"",
        ""
    )

    @Test
    fun testCharCodeBlockFull() = doTest(
        "\"<selection>\\u{0053 0054 0055}</selection>\\u{0053 0054 0055}\"",
        "STU",
        4, 9, 14
    )

    @Test
    fun testCharCodeBlockMulti() = doTest(
        "\"<selection>\\u{0053 0054 0055}\\u{0053 0054 0055}</selection>\"",
        "STUSTU",
        4, 9, 14, 22, 27, 32
    )

    @Test
    fun testCharCodeBlockPartial() = doTest(
        "\"\\u{0053 <selection>0054 0055</selection>}\\u{0053 0054 0055}\"",
        "TU",
        9, 14
    )

    @Test
    fun testCharCodeBlockLeftBoundBroken() = doTest(
        "\"\\u<selection>{0053 0054 0055}</selection>\\u{0053 0054 0055}\"",
        ""
    )

    @Test
    fun testCharCodeBlockMixedParts() = doTest(
        "\"\\u{0053 <selection>0054 0055}\\u{0053 </selection>0054 0055}\"",
        "TU",
        9, 14
    )
}
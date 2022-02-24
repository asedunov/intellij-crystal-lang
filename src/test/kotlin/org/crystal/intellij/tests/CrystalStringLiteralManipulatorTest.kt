package org.crystal.intellij.tests

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.suggested.startOffset
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.psi.CrStringLiteralExpression
import org.junit.Assert
import org.junit.Test

class CrystalStringLiteralManipulatorTest : BasePlatformTestCase() {
    private fun doTest(literalText: String, replacementText: String, newLiteralText: String) {
        myFixture.configureByText("a.cr", literalText)
        val selectionModel = myFixture.editor.selectionModel
        val selectionStart = selectionModel.selectionStart
        val selectionEnd = selectionModel.selectionEnd
        val literal = myFixture.file.findElementAt(selectionStart)!!.parentOfType<CrStringLiteralExpression>()!!
        val literalOffset = literal.startOffset
        val range = TextRange(selectionStart - literalOffset, selectionEnd - literalOffset)
        val newLiteral = WriteCommandAction.runWriteCommandAction(
            myFixture.project,
            Computable { ElementManipulators.handleContentChange(literal, range, replacementText) }
        )
        Assert.assertEquals(newLiteralText, newLiteral.text)
    }

    // "..."

    @Test
    fun testQuotedReplaceWithSimple() = doTest(
        "\"abc<selection>def</selection>ghi\"",
        "xyz",
        "\"abcxyzghi\""
    )

    @Test
    fun testQuotedReplaceWithQuoted() = doTest(
        "\"abc<selection>def</selection>ghi\"",
        "x\"y",
        "\"abcx\\\"yghi\""
    )

    @Test
    fun testQuotedReplaceWithBackslash() = doTest(
        "\"abc<selection>def</selection>ghi\"",
        "x\\y",
        "\"abcx\\\\yghi\""
    )

    @Test
    fun testQuotedRemove() = doTest(
        "\"abc<selection>def</selection>ghi\"",
        "",
        "\"abcghi\""
    )

    @Test
    fun testQuotedAddSimple() = doTest(
        "\"abc<caret>ghi\"",
        "xyz",
        "\"abcxyzghi\""
    )

    @Test
    fun testQuotedReplaceWholeWithSimple() = doTest(
        "\"<selection>abc</selection>\"",
        "xyz",
        "\"xyz\""
    )

    // %(...)

    @Test
    fun testPercentParensReplaceWithSimple() = doTest(
        "%(abc<selection>def</selection>ghi)",
        "xyz",
        "%(abcxyzghi)"
    )

    @Test
    fun testPercentParensReplaceWithBackslash() = doTest(
        "%(abc<selection>def</selection>ghi)",
        "x\\z",
        "%(abcx\\\\zghi)"
    )

    @Test
    fun testPercentParensBalancedReplaceWithParens1() = doTest(
        "%(abc<selection>def</selection>ghi)",
        "(x)(y)",
        "%(abc(x)(y)ghi)"
    )

    @Test
    fun testPercentParensBalancedReplaceWithParens2() = doTest(
        "%(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy",
        "%(abc()xy(f)ghi)"
    )

    @Test
    fun testPercentParensBalancedReplaceWithParens3() = doTest(
        "%(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy",
        "%(abc()xy(f)ghi)"
    )

    @Test
    fun testPercentParensUnbalancedReplaceWithParens1() = doTest(
        "%(abc<selection>def</selection>ghi)",
        "(x)(y",
        "%(abc\\(x\\)\\(yghi)"
    )

    @Test
    fun testPercentParensUnbalancedReplaceWithParens2() = doTest(
        "%(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy(",
        "%(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    @Test
    fun testPercentParensUnbalancedReplaceWithParens3() = doTest(
        "%(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy(",
        "%(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    @Test
    fun testPercentParensRemove() = doTest(
        "%(abc<selection>def</selection>ghi)",
        "",
        "%(abcghi)"
    )

    @Test
    fun testPercentParensAddSimple() = doTest(
        "%(abc<caret>ghi)",
        "xyz",
        "%(abcxyzghi)"
    )

    @Test
    fun testPercentParensReplaceWholeWithSimple() = doTest(
        "%(<selection>abc</selection>)",
        "xyz",
        "%(xyz)"
    )

    // %[...]

    @Test
    fun testPercentBracketsReplaceWithSimple() = doTest(
        "%[abc<selection>def</selection>ghi]",
        "xyz",
        "%[abcxyzghi]"
    )

    @Test
    fun testPercentBracketsReplaceWithBackslash() = doTest(
        "%[abc<selection>def</selection>ghi]",
        "x\\z",
        "%[abcx\\\\zghi]"
    )

    @Test
    fun testPercentBracketsBalancedReplaceWithParens1() = doTest(
        "%[abc<selection>def</selection>ghi]",
        "[x][y]",
        "%[abc[x][y]ghi]"
    )

    @Test
    fun testPercentBracketsBalancedReplaceWithParens2() = doTest(
        "%[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy",
        "%[abc[]xy[f]ghi]"
    )

    @Test
    fun testPercentBracketsBalancedReplaceWithParens3() = doTest(
        "%[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy",
        "%[abc[]xy[f]ghi]"
    )

    @Test
    fun testPercentBracketsUnbalancedReplaceWithParens1() = doTest(
        "%[abc<selection>def</selection>ghi]",
        "[x][y",
        "%[abc\\[x\\]\\[yghi]"
    )

    @Test
    fun testPercentBracketsUnbalancedReplaceWithParens2() = doTest(
        "%[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy[",
        "%[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    @Test
    fun testPercentBracketsUnbalancedReplaceWithParens3() = doTest(
        "%[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy[",
        "%[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    @Test
    fun testPercentBracketsRemove() = doTest(
        "%[abc<selection>def</selection>ghi]",
        "",
        "%[abcghi]"
    )

    @Test
    fun testPercentBracketsAddSimple() = doTest(
        "%[abc<caret>ghi]",
        "xyz",
        "%[abcxyzghi]"
    )

    @Test
    fun testPercentBracketsReplaceWholeWithSimple() = doTest(
        "%[<selection>abc</selection>]",
        "xyz",
        "%[xyz]"
    )

    // %{...}

    @Test
    fun testPercentBracesReplaceWithSimple() = doTest(
        "%{abc<selection>def</selection>ghi}",
        "xyz",
        "%{abcxyzghi}"
    )

    @Test
    fun testPercentBracesReplaceWithBackslash() = doTest(
        "%{abc<selection>def</selection>ghi}",
        "x\\z",
        "%{abcx\\\\zghi}"
    )

    @Test
    fun testPercentBracesBalancedReplaceWithParens1() = doTest(
        "%{abc<selection>def</selection>ghi}",
        "{x}{y}",
        "%{abc{x}{y}ghi}"
    )

    @Test
    fun testPercentBracesBalancedReplaceWithParens2() = doTest(
        "%{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy",
        "%{abc{}xy{f}ghi}"
    )

    @Test
    fun testPercentBracesBalancedReplaceWithParens3() = doTest(
        "%{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy",
        "%{abc{}xy{f}ghi}"
    )

    @Test
    fun testPercentBracesUnbalancedReplaceWithParens1() = doTest(
        "%{abc<selection>def</selection>ghi}",
        "{x}{y",
        "%{abc\\{x\\}\\{yghi}"
    )

    @Test
    fun testPercentBracesUnbalancedReplaceWithParens2() = doTest(
        "%{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy{",
        "%{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    @Test
    fun testPercentBracesUnbalancedReplaceWithParens3() = doTest(
        "%{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy{",
        "%{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    @Test
    fun testPercentBracesRemove() = doTest(
        "%{abc<selection>def</selection>ghi}",
        "",
        "%{abcghi}"
    )

    @Test
    fun testPercentBracesAddSimple() = doTest(
        "%{abc<caret>ghi}",
        "xyz",
        "%{abcxyzghi}"
    )

    @Test
    fun testPercentBracesReplaceWholeWithSimple() = doTest(
        "%{<selection>abc</selection>}",
        "xyz",
        "%{xyz}"
    )

    // %<...>

    @Test
    fun testPercentAnglesReplaceWithSimple() = doTest(
        "%<abc<selection>def</selection>ghi>",
        "xyz",
        "%<abcxyzghi>"
    )

    @Test
    fun testPercentAnglesReplaceWithBackslash() = doTest(
        "%<abc<selection>def</selection>ghi>",
        "x\\z",
        "%<abcx\\\\zghi>"
    )

    @Test
    fun testPercentAnglesBalancedReplaceWithParens1() = doTest(
        "%<abc<selection>def</selection>ghi>",
        "<x><y>",
        "%<abc<x><y>ghi>"
    )

    @Test
    fun testPercentAnglesBalancedReplaceWithParens2() = doTest(
        "%<abc<<selection>d><e></selection><f>ghi>",
        ">xy",
        "%<abc<>xy<f>ghi>"
    )

    @Test
    fun testPercentAnglesBalancedReplaceWithParens3() = doTest(
        "%<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy",
        "%<abc<>xy<f>ghi>"
    )

    @Test
    fun testPercentAnglesUnbalancedReplaceWithParens1() = doTest(
        "%<abc<selection>def</selection>ghi>",
        "<x><y",
        "%<abc\\<x\\>\\<yghi>"
    )

    @Test
    fun testPercentAnglesUnbalancedReplaceWithParens2() = doTest(
        "%<abc<<selection>d><e></selection><f>ghi>",
        ">xy<",
        "%<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    @Test
    fun testPercentAnglesUnbalancedReplaceWithParens3() = doTest(
        "%<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy<",
        "%<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    @Test
    fun testPercentAnglesRemove() = doTest(
        "%<abc<selection>def</selection>ghi>",
        "",
        "%<abcghi>"
    )

    @Test
    fun testPercentAnglesAddSimple() = doTest(
        "%<abc<caret>ghi>",
        "xyz",
        "%<abcxyzghi>"
    )

    @Test
    fun testPercentAnglesReplaceWholeWithSimple() = doTest(
        "%<<selection>abc</selection>>",
        "xyz",
        "%<xyz>"
    )

    // %|...|

    @Test
    fun testPercentPipeReplaceWithSimple() = doTest(
        "%|abc<selection>def</selection>ghi|",
        "xyz",
        "%|abcxyzghi|"
    )

    @Test
    fun testPercentPipeReplaceWithPipe() = doTest(
        "%|abc<selection>def</selection>ghi|",
        "x|y",
        "%|abcx\\|yghi|"
    )

    @Test
    fun testPercentPipeReplaceWithBackslash() = doTest(
        "%|abc<selection>def</selection>ghi|",
        "x\\y",
        "%|abcx\\\\yghi|"
    )

    @Test
    fun testPercentPipeRemove() = doTest(
        "%|abc<selection>def</selection>ghi|",
        "",
        "%|abcghi|"
    )

    @Test
    fun testPercentPipeAddSimple() = doTest(
        "%|abc<caret>ghi|",
        "xyz",
        "%|abcxyzghi|"
    )

    @Test
    fun testPercentPipeReplaceWholeWithSimple() = doTest(
        "%|<selection>abc</selection>|",
        "xyz",
        "%|xyz|"
    )

    // %Q(...)

    @Test
    fun testPercentQParensReplaceWithSimple() = doTest(
        "%Q(abc<selection>def</selection>ghi)",
        "xyz",
        "%Q(abcxyzghi)"
    )

    @Test
    fun testPercentQParensReplaceWithBackslash() = doTest(
        "%Q(abc<selection>def</selection>ghi)",
        "x\\z",
        "%Q(abcx\\\\zghi)"
    )

    @Test
    fun testPercentQParensBalancedReplaceWithParens1() = doTest(
        "%Q(abc<selection>def</selection>ghi)",
        "(x)(y)",
        "%Q(abc(x)(y)ghi)"
    )

    @Test
    fun testPercentQParensBalancedReplaceWithParens2() = doTest(
        "%Q(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy",
        "%Q(abc()xy(f)ghi)"
    )

    @Test
    fun testPercentQParensBalancedReplaceWithParens3() = doTest(
        "%Q(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy",
        "%Q(abc()xy(f)ghi)"
    )

    @Test
    fun testPercentQParensUnbalancedReplaceWithParens1() = doTest(
        "%Q(abc<selection>def</selection>ghi)",
        "(x)(y",
        "%Q(abc\\(x\\)\\(yghi)"
    )

    @Test
    fun testPercentQParensUnbalancedReplaceWithParens2() = doTest(
        "%Q(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy(",
        "%Q(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    @Test
    fun testPercentQParensUnbalancedReplaceWithParens3() = doTest(
        "%Q(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy(",
        "%Q(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    @Test
    fun testPercentQParensRemove() = doTest(
        "%Q(abc<selection>def</selection>ghi)",
        "",
        "%Q(abcghi)"
    )

    @Test
    fun testPercentQParensAddSimple() = doTest(
        "%Q(abc<caret>ghi)",
        "xyz",
        "%Q(abcxyzghi)"
    )

    @Test
    fun testPercentQParensReplaceWholeWithSimple() = doTest(
        "%Q(<selection>abc</selection>)",
        "xyz",
        "%Q(xyz)"
    )

    // %Q[...]

    @Test
    fun testPercentQBracketsReplaceWithSimple() = doTest(
        "%Q[abc<selection>def</selection>ghi]",
        "xyz",
        "%Q[abcxyzghi]"
    )

    @Test
    fun testPercentQBracketsReplaceWithBackslash() = doTest(
        "%Q[abc<selection>def</selection>ghi]",
        "x\\z",
        "%Q[abcx\\\\zghi]"
    )

    @Test
    fun testPercentQBracketsBalancedReplaceWithParens1() = doTest(
        "%Q[abc<selection>def</selection>ghi]",
        "[x][y]",
        "%Q[abc[x][y]ghi]"
    )

    @Test
    fun testPercentQBracketsBalancedReplaceWithParens2() = doTest(
        "%Q[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy",
        "%Q[abc[]xy[f]ghi]"
    )

    @Test
    fun testPercentQBracketsBalancedReplaceWithParens3() = doTest(
        "%Q[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy",
        "%Q[abc[]xy[f]ghi]"
    )

    @Test
    fun testPercentQBracketsUnbalancedReplaceWithParens1() = doTest(
        "%Q[abc<selection>def</selection>ghi]",
        "[x][y",
        "%Q[abc\\[x\\]\\[yghi]"
    )

    @Test
    fun testPercentQBracketsUnbalancedReplaceWithParens2() = doTest(
        "%Q[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy[",
        "%Q[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    @Test
    fun testPercentQBracketsUnbalancedReplaceWithParens3() = doTest(
        "%Q[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy[",
        "%Q[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    @Test
    fun testPercentQBracketsRemove() = doTest(
        "%Q[abc<selection>def</selection>ghi]",
        "",
        "%Q[abcghi]"
    )

    @Test
    fun testPercentQBracketsAddSimple() = doTest(
        "%Q[abc<caret>ghi]",
        "xyz",
        "%Q[abcxyzghi]"
    )

    @Test
    fun testPercentQBracketsReplaceWholeWithSimple() = doTest(
        "%Q[<selection>abc</selection>]",
        "xyz",
        "%Q[xyz]"
    )

    // %Q{...}

    @Test
    fun testPercentQBracesReplaceWithSimple() = doTest(
        "%Q{abc<selection>def</selection>ghi}",
        "xyz",
        "%Q{abcxyzghi}"
    )

    @Test
    fun testPercentQBracesReplaceWithBackslash() = doTest(
        "%Q{abc<selection>def</selection>ghi}",
        "x\\z",
        "%Q{abcx\\\\zghi}"
    )

    @Test
    fun testPercentQBracesBalancedReplaceWithParens1() = doTest(
        "%Q{abc<selection>def</selection>ghi}",
        "{x}{y}",
        "%Q{abc{x}{y}ghi}"
    )

    @Test
    fun testPercentQBracesBalancedReplaceWithParens2() = doTest(
        "%Q{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy",
        "%Q{abc{}xy{f}ghi}"
    )

    @Test
    fun testPercentQBracesBalancedReplaceWithParens3() = doTest(
        "%Q{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy",
        "%Q{abc{}xy{f}ghi}"
    )

    @Test
    fun testPercentQBracesUnbalancedReplaceWithParens1() = doTest(
        "%Q{abc<selection>def</selection>ghi}",
        "{x}{y",
        "%Q{abc\\{x\\}\\{yghi}"
    )

    @Test
    fun testPercentQBracesUnbalancedReplaceWithParens2() = doTest(
        "%Q{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy{",
        "%Q{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    @Test
    fun testPercentQBracesUnbalancedReplaceWithParens3() = doTest(
        "%Q{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy{",
        "%Q{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    @Test
    fun testPercentQBracesRemove() = doTest(
        "%Q{abc<selection>def</selection>ghi}",
        "",
        "%Q{abcghi}"
    )

    @Test
    fun testPercentQBracesAddSimple() = doTest(
        "%Q{abc<caret>ghi}",
        "xyz",
        "%Q{abcxyzghi}"
    )

    @Test
    fun testPercentQBracesReplaceWholeWithSimple() = doTest(
        "%Q{<selection>abc</selection>}",
        "xyz",
        "%Q{xyz}"
    )

    // %Q<...>

    @Test
    fun testPercentQAnglesReplaceWithSimple() = doTest(
        "%Q<abc<selection>def</selection>ghi>",
        "xyz",
        "%Q<abcxyzghi>"
    )

    @Test
    fun testPercentQAnglesReplaceWithBackslash() = doTest(
        "%Q<abc<selection>def</selection>ghi>",
        "x\\z",
        "%Q<abcx\\\\zghi>"
    )

    @Test
    fun testPercentQAnglesBalancedReplaceWithParens1() = doTest(
        "%Q<abc<selection>def</selection>ghi>",
        "<x><y>",
        "%Q<abc<x><y>ghi>"
    )

    @Test
    fun testPercentQAnglesBalancedReplaceWithParens2() = doTest(
        "%Q<abc<<selection>d><e></selection><f>ghi>",
        ">xy",
        "%Q<abc<>xy<f>ghi>"
    )

    @Test
    fun testPercentQAnglesBalancedReplaceWithParens3() = doTest(
        "%Q<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy",
        "%Q<abc<>xy<f>ghi>"
    )

    @Test
    fun testPercentQAnglesUnbalancedReplaceWithParens1() = doTest(
        "%Q<abc<selection>def</selection>ghi>",
        "<x><y",
        "%Q<abc\\<x\\>\\<yghi>"
    )

    @Test
    fun testPercentQAnglesUnbalancedReplaceWithParens2() = doTest(
        "%Q<abc<<selection>d><e></selection><f>ghi>",
        ">xy<",
        "%Q<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    @Test
    fun testPercentQAnglesUnbalancedReplaceWithParens3() = doTest(
        "%Q<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy<",
        "%Q<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    @Test
    fun testPercentQAnglesRemove() = doTest(
        "%Q<abc<selection>def</selection>ghi>",
        "",
        "%Q<abcghi>"
    )

    @Test
    fun testPercentQAnglesAddSimple() = doTest(
        "%Q<abc<caret>ghi>",
        "xyz",
        "%Q<abcxyzghi>"
    )

    @Test
    fun testPercentQAnglesReplaceWholeWithSimple() = doTest(
        "%Q<<selection>abc</selection>>",
        "xyz",
        "%Q<xyz>"
    )

    // %Q|...|

    @Test
    fun testPercentQPipeReplaceWithSimple() = doTest(
        "%Q|abc<selection>def</selection>ghi|",
        "xyz",
        "%Q|abcxyzghi|"
    )

    @Test
    fun testPercentQPipeReplaceWithPipe() = doTest(
        "%Q|abc<selection>def</selection>ghi|",
        "x|y",
        "%Q|abcx\\|yghi|"
    )

    @Test
    fun testPercentQPipeReplaceWithBackslash() = doTest(
        "%Q|abc<selection>def</selection>ghi|",
        "x\\y",
        "%Q|abcx\\\\yghi|"
    )

    @Test
    fun testPercentQPipeRemove() = doTest(
        "%Q|abc<selection>def</selection>ghi|",
        "",
        "%Q|abcghi|"
    )

    @Test
    fun testPercentQPipeAddSimple() = doTest(
        "%Q|abc<caret>ghi|",
        "xyz",
        "%Q|abcxyzghi|"
    )

    @Test
    fun testPercentQPipeReplaceWholeWithSimple() = doTest(
        "%Q|<selection>abc</selection>|",
        "xyz",
        "%Q|xyz|"
    )

    // %q(...)

    @Test
    fun testPercentSmallQParensReplaceWithSimple() = doTest(
        "%q(abc<selection>def</selection>ghi)",
        "xyz",
        "%q(abcxyzghi)"
    )

    @Test
    fun testPercentSmallQParensReplaceWithBackslash() = doTest(
        "%q(abc<selection>def</selection>ghi)",
        "x\\z",
        "%q(abcx\\zghi)"
    )

    @Test
    fun testPercentSmallQParensBalancedReplaceWithParens1() = doTest(
        "%q(abc<selection>def</selection>ghi)",
        "(x)(y)",
        "%q(abc(x)(y)ghi)"
    )

    @Test
    fun testPercentSmallQParensBalancedReplaceWithParens2() = doTest(
        "%q(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy",
        "%q(abc()xy(f)ghi)"
    )

    @Test
    fun testPercentSmallQParensBalancedReplaceWithParens3() = doTest(
        "%q(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy",
        "%q(abc()xy(f)ghi)"
    )

    @Test
    fun testPercentSmallQParensUnbalancedReplaceWithParens1() = doTest(
        "%q(abc<selection>def</selection>ghi)",
        "(x)(y",
        "%(abc\\(x\\)\\(yghi)"
    )

    @Test
    fun testPercentSmallQParensUnbalancedReplaceWithParens2() = doTest(
        "%q(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy(",
        "%(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    @Test
    fun testPercentSmallQParensUnbalancedReplaceWithParens3() = doTest(
        "%q(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy(",
        "%(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    @Test
    fun testPercentSmallQParensUnbalancedReplaceEscapeBackslash() = doTest(
        "%q(a\\c(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy(",
        "%(a\\\\c\\(\\)xy\\(\\(f\\)ghi)"
    )

    @Test
    fun testPercentSmallQParensRemove() = doTest(
        "%q(abc<selection>def</selection>ghi)",
        "",
        "%q(abcghi)"
    )

    @Test
    fun testPercentSmallQParensAddSimple() = doTest(
        "%q(abc<caret>ghi)",
        "xyz",
        "%q(abcxyzghi)"
    )

    @Test
    fun testPercentSmallQParensReplaceWholeWithSimple() = doTest(
        "%q(<selection>abc</selection>)",
        "xyz",
        "%q(xyz)"
    )

    // %q[...]

    @Test
    fun testPercentSmallQBracketsReplaceWithSimple() = doTest(
        "%q[abc<selection>def</selection>ghi]",
        "xyz",
        "%q[abcxyzghi]"
    )

    @Test
    fun testPercentSmallQBracketsReplaceWithBackslash() = doTest(
        "%q[abc<selection>def</selection>ghi]",
        "x\\z",
        "%q[abcx\\zghi]"
    )

    @Test
    fun testPercentSmallQBracketsBalancedReplaceWithBrackets1() = doTest(
        "%q[abc<selection>def</selection>ghi]",
        "[x][y]",
        "%q[abc[x][y]ghi]"
    )

    @Test
    fun testPercentSmallQBracketsBalancedReplaceWithBrackets2() = doTest(
        "%q[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy",
        "%q[abc[]xy[f]ghi]"
    )

    @Test
    fun testPercentSmallQBracketsBalancedReplaceWithBrackets3() = doTest(
        "%q[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy",
        "%q[abc[]xy[f]ghi]"
    )

    @Test
    fun testPercentSmallQBracketsUnbalancedReplaceWithBrackets1() = doTest(
        "%q[abc<selection>def</selection>ghi]",
        "[x][y",
        "%[abc\\[x\\]\\[yghi]"
    )

    @Test
    fun testPercentSmallQBracketsUnbalancedReplaceWithBrackets2() = doTest(
        "%q[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy[",
        "%[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    @Test
    fun testPercentSmallQBracketsUnbalancedReplaceWithBrackets3() = doTest(
        "%q[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy[",
        "%[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    @Test
    fun testPercentSmallQBracketsUnbalancedReplaceEscapeBackslash() = doTest(
        "%q[a\\c[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy[",
        "%[a\\\\c\\[\\]xy\\[\\[f\\]ghi]"
    )

    @Test
    fun testPercentSmallQBracketsRemove() = doTest(
        "%q[abc<selection>def</selection>ghi]",
        "",
        "%q[abcghi]"
    )

    @Test
    fun testPercentSmallQBracketsAddSimple() = doTest(
        "%q[abc<caret>ghi]",
        "xyz",
        "%q[abcxyzghi]"
    )

    @Test
    fun testPercentSmallQBracketsReplaceWholeWithSimple() = doTest(
        "%q[<selection>abc</selection>]",
        "xyz",
        "%q[xyz]"
    )

    // %q{...}

    @Test
    fun testPercentSmallQBracesReplaceWithSimple() = doTest(
        "%q{abc<selection>def</selection>ghi}",
        "xyz",
        "%q{abcxyzghi}"
    )

    @Test
    fun testPercentSmallQBracesReplaceWithBackslash() = doTest(
        "%q{abc<selection>def</selection>ghi}",
        "x\\z",
        "%q{abcx\\zghi}"
    )

    @Test
    fun testPercentSmallQBracesBalancedReplaceWithBraces1() = doTest(
        "%q{abc<selection>def</selection>ghi}",
        "{x}{y}",
        "%q{abc{x}{y}ghi}"
    )

    @Test
    fun testPercentSmallQBracesBalancedReplaceWithBraces2() = doTest(
        "%q{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy",
        "%q{abc{}xy{f}ghi}"
    )

    @Test
    fun testPercentSmallQBracesBalancedReplaceWithBraces3() = doTest(
        "%q{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy",
        "%q{abc{}xy{f}ghi}"
    )

    @Test
    fun testPercentSmallQBracesUnbalancedReplaceWithBraces1() = doTest(
        "%q{abc<selection>def</selection>ghi}",
        "{x}{y",
        "%{abc\\{x\\}\\{yghi}"
    )

    @Test
    fun testPercentSmallQBracesUnbalancedReplaceWithBraces2() = doTest(
        "%q{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy{",
        "%{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    @Test
    fun testPercentSmallQBracesUnbalancedReplaceWithBraces3() = doTest(
        "%q{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy{",
        "%{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    @Test
    fun testPercentSmallQBracesUnbalancedReplaceEscapeBackslash() = doTest(
        "%q{a\\c{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy{",
        "%{a\\\\c\\{\\}xy\\{\\{f\\}ghi}"
    )

    @Test
    fun testPercentSmallQBracesRemove() = doTest(
        "%q{abc<selection>def</selection>ghi}",
        "",
        "%q{abcghi}"
    )

    @Test
    fun testPercentSmallQBracesAddSimple() = doTest(
        "%q{abc<caret>ghi}",
        "xyz",
        "%q{abcxyzghi}"
    )

    @Test
    fun testPercentSmallQBracesReplaceWholeWithSimple() = doTest(
        "%q{<selection>abc</selection>}",
        "xyz",
        "%q{xyz}"
    )

    // %q<...>

    @Test
    fun testPercentSmallQAnglesReplaceWithSimple() = doTest(
        "%q<abc<selection>def</selection>ghi>",
        "xyz",
        "%q<abcxyzghi>"
    )

    @Test
    fun testPercentSmallQAnglesReplaceWithBackslash() = doTest(
        "%q<abc<selection>def</selection>ghi>",
        "x\\z",
        "%q<abcx\\zghi>"
    )

    @Test
    fun testPercentSmallQAnglesBalancedReplaceWithAngles1() = doTest(
        "%q<abc<selection>def</selection>ghi>",
        "<x><y>",
        "%q<abc<x><y>ghi>"
    )

    @Test
    fun testPercentSmallQAnglesBalancedReplaceWithAngles2() = doTest(
        "%q<abc<<selection>d><e></selection><f>ghi>",
        ">xy",
        "%q<abc<>xy<f>ghi>"
    )

    @Test
    fun testPercentSmallQAnglesBalancedReplaceWithAngles3() = doTest(
        "%q<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy",
        "%q<abc<>xy<f>ghi>"
    )

    @Test
    fun testPercentSmallQAnglesUnbalancedReplaceWithAngles1() = doTest(
        "%q<abc<selection>def</selection>ghi>",
        "<x><y",
        "%<abc\\<x\\>\\<yghi>"
    )

    @Test
    fun testPercentSmallQAnglesUnbalancedReplaceWithAngles2() = doTest(
        "%q<abc<<selection>d><e></selection><f>ghi>",
        ">xy<",
        "%<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    @Test
    fun testPercentSmallQAnglesUnbalancedReplaceWithAngles3() = doTest(
        "%q<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy<",
        "%<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    @Test
    fun testPercentSmallQAnglesUnbalancedReplaceEscapeBackslash() = doTest(
        "%q<a\\c<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy<",
        "%<a\\\\c\\<\\>xy\\<\\<f\\>ghi>"
    )

    @Test
    fun testPercentSmallQAnglesRemove() = doTest(
        "%q<abc<selection>def</selection>ghi>",
        "",
        "%q<abcghi>"
    )

    @Test
    fun testPercentSmallQAnglesAddSimple() = doTest(
        "%q<abc<caret>ghi>",
        "xyz",
        "%q<abcxyzghi>"
    )

    @Test
    fun testPercentSmallQAnglesReplaceWholeWithSimple() = doTest(
        "%q<<selection>abc</selection>>",
        "xyz",
        "%q<xyz>"
    )

    // %q|...|

    @Test
    fun testPercentQSmallPipeReplaceWithSimple() = doTest(
        "%q|abc<selection>def</selection>ghi|",
        "xyz",
        "%q|abcxyzghi|"
    )

    @Test
    fun testPercentQSmallPipeReplaceWithPipe() = doTest(
        "%q|abc<selection>def</selection>ghi|",
        "x|y",
        "%|abcx\\|yghi|"
    )

    @Test
    fun testPercentQSmallPipeReplaceWithBackslash() = doTest(
        "%q|abc<selection>def</selection>ghi|",
        "x\\y",
        "%q|abcx\\yghi|"
    )

    @Test
    fun testPercentQSmallPipeRemove() = doTest(
        "%q|abc<selection>def</selection>ghi|",
        "",
        "%q|abcghi|"
    )

    @Test
    fun testPercentQSmallPipeAddSimple() = doTest(
        "%q|abc<caret>ghi|",
        "xyz",
        "%q|abcxyzghi|"
    )

    @Test
    fun testPercentQSmallPipeReplaceWholeWithSimple() = doTest(
        "%q|<selection>abc</selection>|",
        "xyz",
        "%q|xyz|"
    )
}
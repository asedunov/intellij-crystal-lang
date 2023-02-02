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

    fun testQuotedReplaceWithSimple() = doTest(
        "\"abc<selection>def</selection>ghi\"",
        "xyz",
        "\"abcxyzghi\""
    )

    fun testQuotedReplaceWithQuoted() = doTest(
        "\"abc<selection>def</selection>ghi\"",
        "x\"y",
        "\"abcx\\\"yghi\""
    )

    fun testQuotedReplaceWithBackslash() = doTest(
        "\"abc<selection>def</selection>ghi\"",
        "x\\y",
        "\"abcx\\\\yghi\""
    )

    fun testQuotedRemove() = doTest(
        "\"abc<selection>def</selection>ghi\"",
        "",
        "\"abcghi\""
    )

    fun testQuotedAddSimple() = doTest(
        "\"abc<caret>ghi\"",
        "xyz",
        "\"abcxyzghi\""
    )

    fun testQuotedReplaceWholeWithSimple() = doTest(
        "\"<selection>abc</selection>\"",
        "xyz",
        "\"xyz\""
    )

    // %(...)

    fun testPercentParensReplaceWithSimple() = doTest(
        "%(abc<selection>def</selection>ghi)",
        "xyz",
        "%(abcxyzghi)"
    )

    fun testPercentParensReplaceWithBackslash() = doTest(
        "%(abc<selection>def</selection>ghi)",
        "x\\z",
        "%(abcx\\\\zghi)"
    )

    fun testPercentParensBalancedReplaceWithParens1() = doTest(
        "%(abc<selection>def</selection>ghi)",
        "(x)(y)",
        "%(abc(x)(y)ghi)"
    )

    fun testPercentParensBalancedReplaceWithParens2() = doTest(
        "%(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy",
        "%(abc()xy(f)ghi)"
    )

    fun testPercentParensBalancedReplaceWithParens3() = doTest(
        "%(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy",
        "%(abc()xy(f)ghi)"
    )

    fun testPercentParensUnbalancedReplaceWithParens1() = doTest(
        "%(abc<selection>def</selection>ghi)",
        "(x)(y",
        "%(abc\\(x\\)\\(yghi)"
    )

    fun testPercentParensUnbalancedReplaceWithParens2() = doTest(
        "%(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy(",
        "%(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    fun testPercentParensUnbalancedReplaceWithParens3() = doTest(
        "%(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy(",
        "%(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    fun testPercentParensRemove() = doTest(
        "%(abc<selection>def</selection>ghi)",
        "",
        "%(abcghi)"
    )

    fun testPercentParensAddSimple() = doTest(
        "%(abc<caret>ghi)",
        "xyz",
        "%(abcxyzghi)"
    )

    fun testPercentParensReplaceWholeWithSimple() = doTest(
        "%(<selection>abc</selection>)",
        "xyz",
        "%(xyz)"
    )

    // %[...]

    fun testPercentBracketsReplaceWithSimple() = doTest(
        "%[abc<selection>def</selection>ghi]",
        "xyz",
        "%[abcxyzghi]"
    )

    fun testPercentBracketsReplaceWithBackslash() = doTest(
        "%[abc<selection>def</selection>ghi]",
        "x\\z",
        "%[abcx\\\\zghi]"
    )

    fun testPercentBracketsBalancedReplaceWithParens1() = doTest(
        "%[abc<selection>def</selection>ghi]",
        "[x][y]",
        "%[abc[x][y]ghi]"
    )

    fun testPercentBracketsBalancedReplaceWithParens2() = doTest(
        "%[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy",
        "%[abc[]xy[f]ghi]"
    )

    fun testPercentBracketsBalancedReplaceWithParens3() = doTest(
        "%[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy",
        "%[abc[]xy[f]ghi]"
    )

    fun testPercentBracketsUnbalancedReplaceWithParens1() = doTest(
        "%[abc<selection>def</selection>ghi]",
        "[x][y",
        "%[abc\\[x\\]\\[yghi]"
    )

    fun testPercentBracketsUnbalancedReplaceWithParens2() = doTest(
        "%[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy[",
        "%[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    fun testPercentBracketsUnbalancedReplaceWithParens3() = doTest(
        "%[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy[",
        "%[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    fun testPercentBracketsRemove() = doTest(
        "%[abc<selection>def</selection>ghi]",
        "",
        "%[abcghi]"
    )

    fun testPercentBracketsAddSimple() = doTest(
        "%[abc<caret>ghi]",
        "xyz",
        "%[abcxyzghi]"
    )

    fun testPercentBracketsReplaceWholeWithSimple() = doTest(
        "%[<selection>abc</selection>]",
        "xyz",
        "%[xyz]"
    )

    // %{...}

    fun testPercentBracesReplaceWithSimple() = doTest(
        "%{abc<selection>def</selection>ghi}",
        "xyz",
        "%{abcxyzghi}"
    )

    fun testPercentBracesReplaceWithBackslash() = doTest(
        "%{abc<selection>def</selection>ghi}",
        "x\\z",
        "%{abcx\\\\zghi}"
    )

    fun testPercentBracesBalancedReplaceWithParens1() = doTest(
        "%{abc<selection>def</selection>ghi}",
        "{x}{y}",
        "%{abc{x}{y}ghi}"
    )

    fun testPercentBracesBalancedReplaceWithParens2() = doTest(
        "%{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy",
        "%{abc{}xy{f}ghi}"
    )

    fun testPercentBracesBalancedReplaceWithParens3() = doTest(
        "%{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy",
        "%{abc{}xy{f}ghi}"
    )

    fun testPercentBracesUnbalancedReplaceWithParens1() = doTest(
        "%{abc<selection>def</selection>ghi}",
        "{x}{y",
        "%{abc\\{x\\}\\{yghi}"
    )

    fun testPercentBracesUnbalancedReplaceWithParens2() = doTest(
        "%{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy{",
        "%{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    fun testPercentBracesUnbalancedReplaceWithParens3() = doTest(
        "%{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy{",
        "%{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    fun testPercentBracesRemove() = doTest(
        "%{abc<selection>def</selection>ghi}",
        "",
        "%{abcghi}"
    )

    fun testPercentBracesAddSimple() = doTest(
        "%{abc<caret>ghi}",
        "xyz",
        "%{abcxyzghi}"
    )

    fun testPercentBracesReplaceWholeWithSimple() = doTest(
        "%{<selection>abc</selection>}",
        "xyz",
        "%{xyz}"
    )

    // %<...>

    fun testPercentAnglesReplaceWithSimple() = doTest(
        "%<abc<selection>def</selection>ghi>",
        "xyz",
        "%<abcxyzghi>"
    )

    fun testPercentAnglesReplaceWithBackslash() = doTest(
        "%<abc<selection>def</selection>ghi>",
        "x\\z",
        "%<abcx\\\\zghi>"
    )

    fun testPercentAnglesBalancedReplaceWithParens1() = doTest(
        "%<abc<selection>def</selection>ghi>",
        "<x><y>",
        "%<abc<x><y>ghi>"
    )

    fun testPercentAnglesBalancedReplaceWithParens2() = doTest(
        "%<abc<<selection>d><e></selection><f>ghi>",
        ">xy",
        "%<abc<>xy<f>ghi>"
    )

    fun testPercentAnglesBalancedReplaceWithParens3() = doTest(
        "%<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy",
        "%<abc<>xy<f>ghi>"
    )

    fun testPercentAnglesUnbalancedReplaceWithParens1() = doTest(
        "%<abc<selection>def</selection>ghi>",
        "<x><y",
        "%<abc\\<x\\>\\<yghi>"
    )

    fun testPercentAnglesUnbalancedReplaceWithParens2() = doTest(
        "%<abc<<selection>d><e></selection><f>ghi>",
        ">xy<",
        "%<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    fun testPercentAnglesUnbalancedReplaceWithParens3() = doTest(
        "%<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy<",
        "%<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    fun testPercentAnglesRemove() = doTest(
        "%<abc<selection>def</selection>ghi>",
        "",
        "%<abcghi>"
    )

    fun testPercentAnglesAddSimple() = doTest(
        "%<abc<caret>ghi>",
        "xyz",
        "%<abcxyzghi>"
    )

    fun testPercentAnglesReplaceWholeWithSimple() = doTest(
        "%<<selection>abc</selection>>",
        "xyz",
        "%<xyz>"
    )

    // %|...|

    fun testPercentPipeReplaceWithSimple() = doTest(
        "%|abc<selection>def</selection>ghi|",
        "xyz",
        "%|abcxyzghi|"
    )

    fun testPercentPipeReplaceWithPipe() = doTest(
        "%|abc<selection>def</selection>ghi|",
        "x|y",
        "%|abcx\\|yghi|"
    )

    fun testPercentPipeReplaceWithBackslash() = doTest(
        "%|abc<selection>def</selection>ghi|",
        "x\\y",
        "%|abcx\\\\yghi|"
    )

    fun testPercentPipeRemove() = doTest(
        "%|abc<selection>def</selection>ghi|",
        "",
        "%|abcghi|"
    )

    fun testPercentPipeAddSimple() = doTest(
        "%|abc<caret>ghi|",
        "xyz",
        "%|abcxyzghi|"
    )

    fun testPercentPipeReplaceWholeWithSimple() = doTest(
        "%|<selection>abc</selection>|",
        "xyz",
        "%|xyz|"
    )

    // %Q(...)

    fun testPercentQParensReplaceWithSimple() = doTest(
        "%Q(abc<selection>def</selection>ghi)",
        "xyz",
        "%Q(abcxyzghi)"
    )

    fun testPercentQParensReplaceWithBackslash() = doTest(
        "%Q(abc<selection>def</selection>ghi)",
        "x\\z",
        "%Q(abcx\\\\zghi)"
    )

    fun testPercentQParensBalancedReplaceWithParens1() = doTest(
        "%Q(abc<selection>def</selection>ghi)",
        "(x)(y)",
        "%Q(abc(x)(y)ghi)"
    )

    fun testPercentQParensBalancedReplaceWithParens2() = doTest(
        "%Q(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy",
        "%Q(abc()xy(f)ghi)"
    )

    fun testPercentQParensBalancedReplaceWithParens3() = doTest(
        "%Q(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy",
        "%Q(abc()xy(f)ghi)"
    )

    fun testPercentQParensUnbalancedReplaceWithParens1() = doTest(
        "%Q(abc<selection>def</selection>ghi)",
        "(x)(y",
        "%Q(abc\\(x\\)\\(yghi)"
    )

    fun testPercentQParensUnbalancedReplaceWithParens2() = doTest(
        "%Q(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy(",
        "%Q(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    fun testPercentQParensUnbalancedReplaceWithParens3() = doTest(
        "%Q(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy(",
        "%Q(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    fun testPercentQParensRemove() = doTest(
        "%Q(abc<selection>def</selection>ghi)",
        "",
        "%Q(abcghi)"
    )

    fun testPercentQParensAddSimple() = doTest(
        "%Q(abc<caret>ghi)",
        "xyz",
        "%Q(abcxyzghi)"
    )

    fun testPercentQParensReplaceWholeWithSimple() = doTest(
        "%Q(<selection>abc</selection>)",
        "xyz",
        "%Q(xyz)"
    )

    // %Q[...]

    fun testPercentQBracketsReplaceWithSimple() = doTest(
        "%Q[abc<selection>def</selection>ghi]",
        "xyz",
        "%Q[abcxyzghi]"
    )

    fun testPercentQBracketsReplaceWithBackslash() = doTest(
        "%Q[abc<selection>def</selection>ghi]",
        "x\\z",
        "%Q[abcx\\\\zghi]"
    )

    fun testPercentQBracketsBalancedReplaceWithParens1() = doTest(
        "%Q[abc<selection>def</selection>ghi]",
        "[x][y]",
        "%Q[abc[x][y]ghi]"
    )

    fun testPercentQBracketsBalancedReplaceWithParens2() = doTest(
        "%Q[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy",
        "%Q[abc[]xy[f]ghi]"
    )

    fun testPercentQBracketsBalancedReplaceWithParens3() = doTest(
        "%Q[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy",
        "%Q[abc[]xy[f]ghi]"
    )

    fun testPercentQBracketsUnbalancedReplaceWithParens1() = doTest(
        "%Q[abc<selection>def</selection>ghi]",
        "[x][y",
        "%Q[abc\\[x\\]\\[yghi]"
    )

    fun testPercentQBracketsUnbalancedReplaceWithParens2() = doTest(
        "%Q[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy[",
        "%Q[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    fun testPercentQBracketsUnbalancedReplaceWithParens3() = doTest(
        "%Q[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy[",
        "%Q[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    fun testPercentQBracketsRemove() = doTest(
        "%Q[abc<selection>def</selection>ghi]",
        "",
        "%Q[abcghi]"
    )

    fun testPercentQBracketsAddSimple() = doTest(
        "%Q[abc<caret>ghi]",
        "xyz",
        "%Q[abcxyzghi]"
    )

    fun testPercentQBracketsReplaceWholeWithSimple() = doTest(
        "%Q[<selection>abc</selection>]",
        "xyz",
        "%Q[xyz]"
    )

    // %Q{...}

    fun testPercentQBracesReplaceWithSimple() = doTest(
        "%Q{abc<selection>def</selection>ghi}",
        "xyz",
        "%Q{abcxyzghi}"
    )

    fun testPercentQBracesReplaceWithBackslash() = doTest(
        "%Q{abc<selection>def</selection>ghi}",
        "x\\z",
        "%Q{abcx\\\\zghi}"
    )

    fun testPercentQBracesBalancedReplaceWithParens1() = doTest(
        "%Q{abc<selection>def</selection>ghi}",
        "{x}{y}",
        "%Q{abc{x}{y}ghi}"
    )

    fun testPercentQBracesBalancedReplaceWithParens2() = doTest(
        "%Q{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy",
        "%Q{abc{}xy{f}ghi}"
    )

    fun testPercentQBracesBalancedReplaceWithParens3() = doTest(
        "%Q{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy",
        "%Q{abc{}xy{f}ghi}"
    )

    fun testPercentQBracesUnbalancedReplaceWithParens1() = doTest(
        "%Q{abc<selection>def</selection>ghi}",
        "{x}{y",
        "%Q{abc\\{x\\}\\{yghi}"
    )

    fun testPercentQBracesUnbalancedReplaceWithParens2() = doTest(
        "%Q{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy{",
        "%Q{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    fun testPercentQBracesUnbalancedReplaceWithParens3() = doTest(
        "%Q{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy{",
        "%Q{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    fun testPercentQBracesRemove() = doTest(
        "%Q{abc<selection>def</selection>ghi}",
        "",
        "%Q{abcghi}"
    )

    fun testPercentQBracesAddSimple() = doTest(
        "%Q{abc<caret>ghi}",
        "xyz",
        "%Q{abcxyzghi}"
    )

    fun testPercentQBracesReplaceWholeWithSimple() = doTest(
        "%Q{<selection>abc</selection>}",
        "xyz",
        "%Q{xyz}"
    )

    // %Q<...>

    fun testPercentQAnglesReplaceWithSimple() = doTest(
        "%Q<abc<selection>def</selection>ghi>",
        "xyz",
        "%Q<abcxyzghi>"
    )

    fun testPercentQAnglesReplaceWithBackslash() = doTest(
        "%Q<abc<selection>def</selection>ghi>",
        "x\\z",
        "%Q<abcx\\\\zghi>"
    )

    fun testPercentQAnglesBalancedReplaceWithParens1() = doTest(
        "%Q<abc<selection>def</selection>ghi>",
        "<x><y>",
        "%Q<abc<x><y>ghi>"
    )

    fun testPercentQAnglesBalancedReplaceWithParens2() = doTest(
        "%Q<abc<<selection>d><e></selection><f>ghi>",
        ">xy",
        "%Q<abc<>xy<f>ghi>"
    )

    fun testPercentQAnglesBalancedReplaceWithParens3() = doTest(
        "%Q<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy",
        "%Q<abc<>xy<f>ghi>"
    )

    fun testPercentQAnglesUnbalancedReplaceWithParens1() = doTest(
        "%Q<abc<selection>def</selection>ghi>",
        "<x><y",
        "%Q<abc\\<x\\>\\<yghi>"
    )

    fun testPercentQAnglesUnbalancedReplaceWithParens2() = doTest(
        "%Q<abc<<selection>d><e></selection><f>ghi>",
        ">xy<",
        "%Q<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    fun testPercentQAnglesUnbalancedReplaceWithParens3() = doTest(
        "%Q<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy<",
        "%Q<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    fun testPercentQAnglesRemove() = doTest(
        "%Q<abc<selection>def</selection>ghi>",
        "",
        "%Q<abcghi>"
    )

    fun testPercentQAnglesAddSimple() = doTest(
        "%Q<abc<caret>ghi>",
        "xyz",
        "%Q<abcxyzghi>"
    )

    fun testPercentQAnglesReplaceWholeWithSimple() = doTest(
        "%Q<<selection>abc</selection>>",
        "xyz",
        "%Q<xyz>"
    )

    // %Q|...|

    fun testPercentQPipeReplaceWithSimple() = doTest(
        "%Q|abc<selection>def</selection>ghi|",
        "xyz",
        "%Q|abcxyzghi|"
    )

    fun testPercentQPipeReplaceWithPipe() = doTest(
        "%Q|abc<selection>def</selection>ghi|",
        "x|y",
        "%Q|abcx\\|yghi|"
    )

    fun testPercentQPipeReplaceWithBackslash() = doTest(
        "%Q|abc<selection>def</selection>ghi|",
        "x\\y",
        "%Q|abcx\\\\yghi|"
    )

    fun testPercentQPipeRemove() = doTest(
        "%Q|abc<selection>def</selection>ghi|",
        "",
        "%Q|abcghi|"
    )

    fun testPercentQPipeAddSimple() = doTest(
        "%Q|abc<caret>ghi|",
        "xyz",
        "%Q|abcxyzghi|"
    )

    fun testPercentQPipeReplaceWholeWithSimple() = doTest(
        "%Q|<selection>abc</selection>|",
        "xyz",
        "%Q|xyz|"
    )

    // %q(...)

    fun testPercentSmallQParensReplaceWithSimple() = doTest(
        "%q(abc<selection>def</selection>ghi)",
        "xyz",
        "%q(abcxyzghi)"
    )

    fun testPercentSmallQParensReplaceWithBackslash() = doTest(
        "%q(abc<selection>def</selection>ghi)",
        "x\\z",
        "%q(abcx\\zghi)"
    )

    fun testPercentSmallQParensBalancedReplaceWithParens1() = doTest(
        "%q(abc<selection>def</selection>ghi)",
        "(x)(y)",
        "%q(abc(x)(y)ghi)"
    )

    fun testPercentSmallQParensBalancedReplaceWithParens2() = doTest(
        "%q(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy",
        "%q(abc()xy(f)ghi)"
    )

    fun testPercentSmallQParensBalancedReplaceWithParens3() = doTest(
        "%q(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy",
        "%q(abc()xy(f)ghi)"
    )

    fun testPercentSmallQParensUnbalancedReplaceWithParens1() = doTest(
        "%q(abc<selection>def</selection>ghi)",
        "(x)(y",
        "%(abc\\(x\\)\\(yghi)"
    )

    fun testPercentSmallQParensUnbalancedReplaceWithParens2() = doTest(
        "%q(abc(<selection>d)(e)</selection>(f)ghi)",
        ")xy(",
        "%(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    fun testPercentSmallQParensUnbalancedReplaceWithParens3() = doTest(
        "%q(abc(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy(",
        "%(abc\\(\\)xy\\(\\(f\\)ghi)"
    )

    fun testPercentSmallQParensUnbalancedReplaceEscapeBackslash() = doTest(
        "%q(a\\c(<selection>d\\)\\(e)</selection>(f)ghi)",
        ")xy(",
        "%(a\\\\c\\(\\)xy\\(\\(f\\)ghi)"
    )

    fun testPercentSmallQParensRemove() = doTest(
        "%q(abc<selection>def</selection>ghi)",
        "",
        "%q(abcghi)"
    )

    fun testPercentSmallQParensAddSimple() = doTest(
        "%q(abc<caret>ghi)",
        "xyz",
        "%q(abcxyzghi)"
    )

    fun testPercentSmallQParensReplaceWholeWithSimple() = doTest(
        "%q(<selection>abc</selection>)",
        "xyz",
        "%q(xyz)"
    )

    // %q[...]

    fun testPercentSmallQBracketsReplaceWithSimple() = doTest(
        "%q[abc<selection>def</selection>ghi]",
        "xyz",
        "%q[abcxyzghi]"
    )

    fun testPercentSmallQBracketsReplaceWithBackslash() = doTest(
        "%q[abc<selection>def</selection>ghi]",
        "x\\z",
        "%q[abcx\\zghi]"
    )

    fun testPercentSmallQBracketsBalancedReplaceWithBrackets1() = doTest(
        "%q[abc<selection>def</selection>ghi]",
        "[x][y]",
        "%q[abc[x][y]ghi]"
    )

    fun testPercentSmallQBracketsBalancedReplaceWithBrackets2() = doTest(
        "%q[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy",
        "%q[abc[]xy[f]ghi]"
    )

    fun testPercentSmallQBracketsBalancedReplaceWithBrackets3() = doTest(
        "%q[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy",
        "%q[abc[]xy[f]ghi]"
    )

    fun testPercentSmallQBracketsUnbalancedReplaceWithBrackets1() = doTest(
        "%q[abc<selection>def</selection>ghi]",
        "[x][y",
        "%[abc\\[x\\]\\[yghi]"
    )

    fun testPercentSmallQBracketsUnbalancedReplaceWithBrackets2() = doTest(
        "%q[abc[<selection>d][e]</selection>[f]ghi]",
        "]xy[",
        "%[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    fun testPercentSmallQBracketsUnbalancedReplaceWithBrackets3() = doTest(
        "%q[abc[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy[",
        "%[abc\\[\\]xy\\[\\[f\\]ghi]"
    )

    fun testPercentSmallQBracketsUnbalancedReplaceEscapeBackslash() = doTest(
        "%q[a\\c[<selection>d\\]\\[e]</selection>[f]ghi]",
        "]xy[",
        "%[a\\\\c\\[\\]xy\\[\\[f\\]ghi]"
    )

    fun testPercentSmallQBracketsRemove() = doTest(
        "%q[abc<selection>def</selection>ghi]",
        "",
        "%q[abcghi]"
    )

    fun testPercentSmallQBracketsAddSimple() = doTest(
        "%q[abc<caret>ghi]",
        "xyz",
        "%q[abcxyzghi]"
    )

    fun testPercentSmallQBracketsReplaceWholeWithSimple() = doTest(
        "%q[<selection>abc</selection>]",
        "xyz",
        "%q[xyz]"
    )

    // %q{...}

    fun testPercentSmallQBracesReplaceWithSimple() = doTest(
        "%q{abc<selection>def</selection>ghi}",
        "xyz",
        "%q{abcxyzghi}"
    )

    fun testPercentSmallQBracesReplaceWithBackslash() = doTest(
        "%q{abc<selection>def</selection>ghi}",
        "x\\z",
        "%q{abcx\\zghi}"
    )

    fun testPercentSmallQBracesBalancedReplaceWithBraces1() = doTest(
        "%q{abc<selection>def</selection>ghi}",
        "{x}{y}",
        "%q{abc{x}{y}ghi}"
    )

    fun testPercentSmallQBracesBalancedReplaceWithBraces2() = doTest(
        "%q{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy",
        "%q{abc{}xy{f}ghi}"
    )

    fun testPercentSmallQBracesBalancedReplaceWithBraces3() = doTest(
        "%q{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy",
        "%q{abc{}xy{f}ghi}"
    )

    fun testPercentSmallQBracesUnbalancedReplaceWithBraces1() = doTest(
        "%q{abc<selection>def</selection>ghi}",
        "{x}{y",
        "%{abc\\{x\\}\\{yghi}"
    )

    fun testPercentSmallQBracesUnbalancedReplaceWithBraces2() = doTest(
        "%q{abc{<selection>d}{e}</selection>{f}ghi}",
        "}xy{",
        "%{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    fun testPercentSmallQBracesUnbalancedReplaceWithBraces3() = doTest(
        "%q{abc{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy{",
        "%{abc\\{\\}xy\\{\\{f\\}ghi}"
    )

    fun testPercentSmallQBracesUnbalancedReplaceEscapeBackslash() = doTest(
        "%q{a\\c{<selection>d\\}\\{e}</selection>{f}ghi}",
        "}xy{",
        "%{a\\\\c\\{\\}xy\\{\\{f\\}ghi}"
    )

    fun testPercentSmallQBracesRemove() = doTest(
        "%q{abc<selection>def</selection>ghi}",
        "",
        "%q{abcghi}"
    )

    fun testPercentSmallQBracesAddSimple() = doTest(
        "%q{abc<caret>ghi}",
        "xyz",
        "%q{abcxyzghi}"
    )

    fun testPercentSmallQBracesReplaceWholeWithSimple() = doTest(
        "%q{<selection>abc</selection>}",
        "xyz",
        "%q{xyz}"
    )

    // %q<...>

    fun testPercentSmallQAnglesReplaceWithSimple() = doTest(
        "%q<abc<selection>def</selection>ghi>",
        "xyz",
        "%q<abcxyzghi>"
    )

    fun testPercentSmallQAnglesReplaceWithBackslash() = doTest(
        "%q<abc<selection>def</selection>ghi>",
        "x\\z",
        "%q<abcx\\zghi>"
    )

    fun testPercentSmallQAnglesBalancedReplaceWithAngles1() = doTest(
        "%q<abc<selection>def</selection>ghi>",
        "<x><y>",
        "%q<abc<x><y>ghi>"
    )

    fun testPercentSmallQAnglesBalancedReplaceWithAngles2() = doTest(
        "%q<abc<<selection>d><e></selection><f>ghi>",
        ">xy",
        "%q<abc<>xy<f>ghi>"
    )

    fun testPercentSmallQAnglesBalancedReplaceWithAngles3() = doTest(
        "%q<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy",
        "%q<abc<>xy<f>ghi>"
    )

    fun testPercentSmallQAnglesUnbalancedReplaceWithAngles1() = doTest(
        "%q<abc<selection>def</selection>ghi>",
        "<x><y",
        "%<abc\\<x\\>\\<yghi>"
    )

    fun testPercentSmallQAnglesUnbalancedReplaceWithAngles2() = doTest(
        "%q<abc<<selection>d><e></selection><f>ghi>",
        ">xy<",
        "%<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    fun testPercentSmallQAnglesUnbalancedReplaceWithAngles3() = doTest(
        "%q<abc<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy<",
        "%<abc\\<\\>xy\\<\\<f\\>ghi>"
    )

    fun testPercentSmallQAnglesUnbalancedReplaceEscapeBackslash() = doTest(
        "%q<a\\c<<selection>d\\>\\<e></selection><f>ghi>",
        ">xy<",
        "%<a\\\\c\\<\\>xy\\<\\<f\\>ghi>"
    )

    fun testPercentSmallQAnglesRemove() = doTest(
        "%q<abc<selection>def</selection>ghi>",
        "",
        "%q<abcghi>"
    )

    fun testPercentSmallQAnglesAddSimple() = doTest(
        "%q<abc<caret>ghi>",
        "xyz",
        "%q<abcxyzghi>"
    )

    fun testPercentSmallQAnglesReplaceWholeWithSimple() = doTest(
        "%q<<selection>abc</selection>>",
        "xyz",
        "%q<xyz>"
    )

    // %q|...|

    fun testPercentQSmallPipeReplaceWithSimple() = doTest(
        "%q|abc<selection>def</selection>ghi|",
        "xyz",
        "%q|abcxyzghi|"
    )

    fun testPercentQSmallPipeReplaceWithPipe() = doTest(
        "%q|abc<selection>def</selection>ghi|",
        "x|y",
        "%|abcx\\|yghi|"
    )

    fun testPercentQSmallPipeReplaceWithBackslash() = doTest(
        "%q|abc<selection>def</selection>ghi|",
        "x\\y",
        "%q|abcx\\yghi|"
    )

    fun testPercentQSmallPipeRemove() = doTest(
        "%q|abc<selection>def</selection>ghi|",
        "",
        "%q|abcghi|"
    )

    fun testPercentQSmallPipeAddSimple() = doTest(
        "%q|abc<caret>ghi|",
        "xyz",
        "%q|abcxyzghi|"
    )

    fun testPercentQSmallPipeReplaceWholeWithSimple() = doTest(
        "%q|<selection>abc</selection>|",
        "xyz",
        "%q|xyz|"
    )
}
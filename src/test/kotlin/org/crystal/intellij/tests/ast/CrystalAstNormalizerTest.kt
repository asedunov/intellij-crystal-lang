package org.crystal.intellij.tests.ast

import org.crystal.intellij.lang.ast.nodes.CstAssign
import org.crystal.intellij.lang.ast.nodes.CstCall
import org.crystal.intellij.lang.ast.nodes.CstExpressions
import org.crystal.intellij.lang.ast.nodes.CstNode
import org.crystal.intellij.lang.ast.normalize
import org.crystal.intellij.util.cast

class CrystalAstNormalizerTest : CrystalCstParsingTestBase() {
    private fun CstNode.normalize() = normalize(project)

    private infix fun String.becomes(expected: String) {
        convert(this).normalize().assertRenderTrimmed(expected)
    }

    private inline fun <reified T> assertNormalizedNode(source: String, body: T.(String) -> Unit) {
        convert(source).normalize().cast<T>().body(source)
    }

    private fun assertNormalizedExpressions(source: String, body: CstExpressions.(String) -> Unit) {
        assertNormalizedNode(source, body)
    }

    fun testHeredoc() {
        "<<-FOO\nhello\nFOO" becomes "\"hello\""
    }

    fun testUnless() {
        "unless 1; 2; end" becomes "if 1\nelse\n  2\nend"
    }

    fun testUntil() {
        "until 1; 2; end" becomes "while !1\n  2\nend"
    }

    fun testControlExpressions() {
        listOf("return", "next", "break").forEach { keyword ->
            "$keyword 1; 2" becomes "$keyword 1"
        }
    }

    fun testDontRemoveAfterReturnWithUnless() {
        "return 1 unless 2; 3" becomes "if 2\nelse\n  return 1\nend\n3"
    }

    fun testRemoveAfterIfWithReturnInBothBranches() {
        "if true; break; else; return; end; 1" becomes "if true\n  break\nelse\n  return\nend"
    }

    fun testDontRemoveAfterIfWithReturnInOneBranch() {
        "if true; 1; else; return; end; 1" becomes "if true\n  1\nelse\n  return\nend\n1"
    }

    fun testBlockUnpackingWithEmptyBody() {
        """
        foo do |(x, y), z|
        end
        """.trimIndent() becomes """
        foo do |__temp_1, z|
          x, y = __temp_1
        end
        """.trimIndent()
    }

    fun testBlockUnpackingWithSingleExpressionBody() {
        """
        foo do |(x, y), z|
          z
        end
        """.trimIndent() becomes """
        foo do |__temp_1, z|
          x, y = __temp_1
          z
        end
        """.trimIndent()
    }

    fun testBlockUnpackingWithMultipleExpressionsBody() {
        """
        foo do |(x, y), z|
          x
          y
          z
        end
        """.trimIndent() becomes """
        foo do |__temp_1, z|
          x, y = __temp_1
          x
          y
          z
        end
        """.trimIndent()
    }

    fun testBlockUnpackingWithUnderscore() {
        """
        foo do |(x, _), z|
        end
        """.trimIndent() becomes """
        foo do |__temp_1, z|
          x, _ = __temp_1
        end
        """.trimIndent()
    }

    fun testBlockNestedUnpacking() {
        """
        foo do |(a, (b, c))|
          1
        end
        """.trimIndent() becomes """
        foo do |__temp_1|
          a, __temp_2 = __temp_1
          b, c = __temp_2
          1
        end
        """.trimIndent()
    }

    fun testBlockWithMultipleNestedUnpackings() {
        """
        foo do |(a, (b, (c, (d, e)), f))|
          1
        end
        """.trimIndent() becomes """
        foo do |__temp_1|
          a, __temp_2 = __temp_1
          b, __temp_3, f = __temp_2
          c, __temp_4 = __temp_3
          d, e = __temp_4
          1
        end
        """.trimIndent()
    }

    fun testBlockUnpackingWithSplat() {
        """
        foo do |(x, *y, z)|
        end
        """.trimIndent() becomes """
        foo do |__temp_1|
          x, *y, z = __temp_1
        end
        """.trimIndent()
    }

    fun testEmptyExpressionWithBeginEnd() {
        "begin\nend" becomes "begin\nend"
    }

    fun testExpression() {
        "(1 < 2).as(Bool)" becomes "(1 < 2).as(Bool)"
    }

    fun testExpressionsWithBeginEnd() {
        "begin\n  1\n  2\nend" becomes "begin\n  1\n  2\nend"
    }

    fun testChainedComparisonWithLiteral() {
        "1 <= 2 <= 3" becomes "1 <= 2 && 2 <= 3"
    }

    fun testChainedComparisonWithVar() {
        "b = 1; 1 <= b <= 3" becomes "b = 1\n1 <= b && b <= 3"
    }

    fun testChainedComparisonWithCall() {
        "1 <= b <= 3" becomes "1 <= (__temp_1 = b) && __temp_1 <= 3"
    }

    fun testTwoChainedComparisonsWithLiteral() {
        "1 <= 2 <= 3 <= 4" becomes "(1 <= 2 && 2 <= 3) && 3 <= 4"
    }

    fun testTwoChainedComparisonsWithCalls() {
        "1 <= a <= b <= 4" becomes "(1 <= (__temp_2 = a) && __temp_2 <= (__temp_1 = b)) && __temp_1 <= 4"
    }

    fun testOpAssignVar() {
        for (op in listOf("+", "-", "*", "&+", "&-", "&*")) {
            assertNormalizedExpressions("a = 1; a $op= 2") {
                assertRenderTrimmed("a = 1\na = a $op 2")
                expressions[1].cast<CstAssign>().value.assertNameStart(1, 10)
            }
        }
    }

    fun testOpAssignVarOr() {
        "a = 1; a ||= 2" becomes "a = 1\na || (a = 2)"
    }

    fun testOpAssignVarAnd() {
        "a = 1; a &&= 2" becomes "a = 1\na && (a = 2)"
    }

    fun testOpAssignExpDotValuePlus() = assertNormalizedExpressions("a.b += 1") {
        assertRenderTrimmed("__temp_1 = a\n__temp_1.b = __temp_1.b + 1")
        expressions[1].cast<CstCall>().args[0].assertNameStart(1, 5)
    }

    fun testOpAssignVarDotValuePlus() = assertNormalizedExpressions("a = 1; a.b += 2") {
        assertRenderTrimmed("a = 1\na.b = a.b + 2")
        expressions[1].cast<CstCall>().args[0].assertNameStart(1, 12)
    }

    fun testOpAssignInstanceVarDotValuePlus() = assertNormalizedNode<CstCall>("@a.b += 2") {
        assertRenderTrimmed("@a.b = @a.b + 2")
        args[0].assertNameStart(1, 6)
    }

    fun testOpAssignClassVarDotValuePlus() = assertNormalizedNode<CstCall>("@@a.b += 2") {
        assertRenderTrimmed("@@a.b = @@a.b + 2")
        args[0].assertNameStart(1, 7)
    }

    fun testOpAssignExpDotValueOr() {
        "a.b ||= 1" becomes "__temp_1 = a\n__temp_1.b || (__temp_1.b = 1)"
    }

    fun testOpAssignExpDotValueAnd() {
        "a.b &&= 1" becomes "__temp_1 = a\n__temp_1.b && (__temp_1.b = 1)"
    }

    fun testOpAssignIndexedExpPlus() = assertNormalizedExpressions("a[b, c] += 1") {
        assertRenderTrimmed("__temp_1 = b\n__temp_2 = c\n__temp_3 = a\n__temp_3[__temp_1, __temp_2] = __temp_3[__temp_1, __temp_2] + 1")
        expressions[3].cast<CstCall>().args[2].assertNameStart(1, 9)
    }

    fun testOpAssignIndexedExpOr() {
        "a[b, c] ||= 1" becomes "__temp_1 = b\n__temp_2 = c\n__temp_3 = a\n__temp_3[__temp_1, __temp_2]? || (__temp_3[__temp_1, __temp_2] = 1)"
    }

    fun testOpAssignIndexedExpAnd() {
        "a[b, c] &&= 1" becomes "__temp_1 = b\n__temp_2 = c\n__temp_3 = a\n__temp_3[__temp_1, __temp_2]? && (__temp_3[__temp_1, __temp_2] = 1)"
    }

    fun testOpAssignExpIndexedByLiteralPlus() = assertNormalizedExpressions("a[0] += 1") {
        assertRenderTrimmed("__temp_2 = a\n__temp_2[0] = __temp_2[0] + 1")
        expressions[1].cast<CstCall>().args[1].assertNameStart(1, 6)
    }

    fun testOpAssignVarIndexedByLiteralPlus() = assertNormalizedExpressions("a = 1; a[0] += 1") {
        assertRenderTrimmed("a = 1\na[0] = a[0] + 1")
        expressions[1].cast<CstCall>().args[1].assertNameStart(1, 13)
    }

    fun testOpAssignInstanceVarIndexedByLiteralPlus() = assertNormalizedNode<CstCall>("@a[0] += 1") {
        assertRenderTrimmed("@a[0] = @a[0] + 1")
        args[1].assertNameStart(1, 7)
    }

    fun testOpAssignClassVarIndexedByLiteralPlus() = assertNormalizedNode<CstCall>("@@a[0] += 1") {
        assertRenderTrimmed("@@a[0] = @@a[0] + 1")
        args[1].assertNameStart(1, 8)
    }
}
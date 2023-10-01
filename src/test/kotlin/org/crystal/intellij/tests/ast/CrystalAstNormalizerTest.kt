package org.crystal.intellij.tests.ast

import org.crystal.intellij.lang.ast.expandLiteral
import org.crystal.intellij.lang.ast.expandLiteralNamed
import org.crystal.intellij.lang.ast.nodes.CstAssign
import org.crystal.intellij.lang.ast.nodes.CstCall
import org.crystal.intellij.lang.ast.nodes.CstExpressions
import org.crystal.intellij.lang.ast.nodes.CstNode
import org.crystal.intellij.lang.ast.normalize
import org.crystal.intellij.lang.config.CrystalFlags
import org.crystal.intellij.tests.util.withFlag
import org.crystal.intellij.util.cast

class CrystalAstNormalizerTest : CrystalCstParsingTestBase() {
    private fun CstNode<*>.normalize() = normalize(project)

    private fun CstNode<*>.expandLiteral() = expandLiteral(project)

    private fun CstNode<*>.expandLiteralNamed(
        genericType: CstNode<*>? = null
    ) = expandLiteralNamed(project, genericType)

    private infix fun String.normalizesTo(expected: String) {
        convert(this).normalize().assertRenderTrimmed(expected)
    }

    private infix fun CstNode<*>.expandsLiteralTo(expected: String) {
        expandLiteral().assertRenderTrimmed(expected)
    }

    private fun CstNode<*>.expandsLiteralNamedTo(expected: String, genericType: CstNode<*>? = null) {
        expandLiteralNamed(genericType).assertRenderTrimmed(expected)
    }

    private infix fun String.expandsLiteralTo(expected: String) {
        convert(this).expandsLiteralTo(expected)
    }

    private fun String.expandsLiteralNamedTo(expected: String, genericType: CstNode<*>? = null) {
        convert(this).expandsLiteralNamedTo(expected, genericType)
    }

    private infix fun String.expandsLiteralNamedTo(expected: String) {
        expandsLiteralNamedTo(expected, null)
    }

    private inline fun <reified T> assertNormalizedNode(source: String, body: T.(String) -> Unit) {
        convert(source).normalize().cast<T>().body(source)
    }

    private fun assertNormalizedExpressions(source: String, body: CstExpressions.(String) -> Unit) {
        assertNormalizedNode(source, body)
    }

    fun testHeredoc() {
        "<<-FOO\nhello\nFOO" normalizesTo "\"hello\""
    }

    fun testUnless() {
        "unless 1; 2; end" normalizesTo "if 1\nelse\n  2\nend"
    }

    fun testUntil() {
        "until 1; 2; end" normalizesTo "while !1\n  2\nend"
    }

    fun testControlExpressions() {
        listOf("return", "next", "break").forEach { keyword ->
            "$keyword 1; 2" normalizesTo "$keyword 1"
        }
    }

    fun testDontRemoveAfterReturnWithUnless() {
        "return 1 unless 2; 3" normalizesTo "if 2\nelse\n  return 1\nend\n3"
    }

    fun testRemoveAfterIfWithReturnInBothBranches() {
        "if true; break; else; return; end; 1" normalizesTo "if true\n  break\nelse\n  return\nend"
    }

    fun testDontRemoveAfterIfWithReturnInOneBranch() {
        "if true; 1; else; return; end; 1" normalizesTo "if true\n  1\nelse\n  return\nend\n1"
    }

    fun testBlockUnpackingWithEmptyBody() {
        """
        foo do |(x, y), z|
        end
        """.trimIndent() normalizesTo """
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
        """.trimIndent() normalizesTo """
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
        """.trimIndent() normalizesTo """
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
        """.trimIndent() normalizesTo """
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
        """.trimIndent() normalizesTo """
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
        """.trimIndent() normalizesTo """
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
        """.trimIndent() normalizesTo """
        foo do |__temp_1|
          x, *y, z = __temp_1
        end
        """.trimIndent()
    }

    fun testEmptyExpressionWithBeginEnd() {
        "begin\nend" normalizesTo "begin\nend"
    }

    fun testExpression() {
        "(1 < 2).as(Bool)" normalizesTo "(1 < 2).as(Bool)"
    }

    fun testExpressionsWithBeginEnd() {
        "begin\n  1\n  2\nend" normalizesTo "begin\n  1\n  2\nend"
    }

    fun testChainedComparisonWithLiteral() {
        "1 <= 2 <= 3" normalizesTo "1 <= 2 && 2 <= 3"
    }

    fun testChainedComparisonWithVar() {
        "b = 1; 1 <= b <= 3" normalizesTo "b = 1\n1 <= b && b <= 3"
    }

    fun testChainedComparisonWithCall() {
        "1 <= b <= 3" normalizesTo "1 <= (__temp_1 = b) && __temp_1 <= 3"
    }

    fun testTwoChainedComparisonsWithLiteral() {
        "1 <= 2 <= 3 <= 4" normalizesTo "(1 <= 2 && 2 <= 3) && 3 <= 4"
    }

    fun testTwoChainedComparisonsWithCalls() {
        "1 <= a <= b <= 4" normalizesTo "(1 <= (__temp_2 = a) && __temp_2 <= (__temp_1 = b)) && __temp_1 <= 4"
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
        "a = 1; a ||= 2" normalizesTo "a = 1\na || (a = 2)"
    }

    fun testOpAssignVarAnd() {
        "a = 1; a &&= 2" normalizesTo "a = 1\na && (a = 2)"
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
        "a.b ||= 1" normalizesTo "__temp_1 = a\n__temp_1.b || (__temp_1.b = 1)"
    }

    fun testOpAssignExpDotValueAnd() {
        "a.b &&= 1" normalizesTo "__temp_1 = a\n__temp_1.b && (__temp_1.b = 1)"
    }

    fun testOpAssignIndexedExpPlus() = assertNormalizedExpressions("a[b, c] += 1") {
        assertRenderTrimmed("__temp_1 = b\n__temp_2 = c\n__temp_3 = a\n__temp_3[__temp_1, __temp_2] = __temp_3[__temp_1, __temp_2] + 1")
        expressions[3].cast<CstCall>().args[2].assertNameStart(1, 9)
    }

    fun testOpAssignIndexedExpOr() {
        "a[b, c] ||= 1" normalizesTo "__temp_1 = b\n__temp_2 = c\n__temp_3 = a\n__temp_3[__temp_1, __temp_2]? || (__temp_3[__temp_1, __temp_2] = 1)"
    }

    fun testOpAssignIndexedExpAnd() {
        "a[b, c] &&= 1" normalizesTo "__temp_1 = b\n__temp_2 = c\n__temp_3 = a\n__temp_3[__temp_1, __temp_2]? && (__temp_3[__temp_1, __temp_2] = 1)"
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

    fun testExpandAndWithoutVariable() {
        "a && b" expandsLiteralTo "if __temp_1 = a\n  b\nelse\n  __temp_1\nend"
    }

    fun testExpandAndWithVarOnLeft() = assertExpressions("a = 1; a && b") {
        expressions[1] expandsLiteralTo "if a\n  b\nelse\n  a\nend"
    }

    fun testExpandAndWithIsAOnVar() = assertExpressions("a = 1; a.is_a?(Foo) && b") {
        expressions[1] expandsLiteralTo "if a.is_a?(Foo)\n  b\nelse\n  a.is_a?(Foo)\nend"
    }

    fun testExpandAndWithNotOnVar() = assertExpressions("a = 1; !a && b") {
        expressions[1] expandsLiteralTo "if !a\n  b\nelse\n  !a\nend"
    }

    fun testExpandAndWithNotOnIsAOnVar() = assertExpressions("a = 1; !a.is_a?(Int32) && b") {
        expressions[1] expandsLiteralTo "if !a.is_a?(Int32)\n  b\nelse\n  !a.is_a?(Int32)\nend"
    }

    fun testExpandAndWithIsAOnExp() = assertExpressions("a = 1; 1.is_a?(Foo) && b") {
        expressions[1] expandsLiteralTo "if __temp_1 = 1.is_a?(Foo)\n  b\nelse\n  __temp_1\nend"
    }

    fun testExpandAndWithAssign() {
        "(a = 1) && b" expandsLiteralTo "if a = 1\n  b\nelse\n  a\nend"
    }

    fun testExpandOrWithoutVariable() {
        "a || b" expandsLiteralTo "if __temp_1 = a\n  __temp_1\nelse\n  b\nend"
    }

    fun testExpandOrWithVarOnLeft() = assertExpressions("a = 1; a || b") {
        expressions[1] expandsLiteralTo "if a\n  a\nelse\n  b\nend"
    }

    fun testExpandOrWithIsAOnVar() = assertExpressions("a = 1; a.is_a?(Foo) || b") {
        expressions[1] expandsLiteralTo "if a.is_a?(Foo)\n  a.is_a?(Foo)\nelse\n  b\nend"
    }

    fun testExpandOrWithNotOnVar() = assertExpressions("a = 1; !a || b") {
        expressions[1] expandsLiteralTo "if !a\n  !a\nelse\n  b\nend"
    }

    fun testExpandOrWithNotOnIsAOnVar() = assertExpressions("a = 1; !a.is_a?(Int32) || b") {
        expressions[1] expandsLiteralTo "if !a.is_a?(Int32)\n  !a.is_a?(Int32)\nelse\n  b\nend"
    }

    fun testExpandOrWithIsAOnExp() = assertExpressions("a = 1; 1.is_a?(Foo) || b") {
        expressions[1] expandsLiteralTo "if __temp_1 = 1.is_a?(Foo)\n  __temp_1\nelse\n  b\nend"
    }

    fun testExpandOrWithAssign() {
        "(a = 1) || b" expandsLiteralTo "if a = 1\n  a\nelse\n  b\nend"
    }

    fun testExpandRangeInclusive() {
        "1..2" expandsLiteralTo "::Range.new(1, 2, false)"
    }

    fun testExpandRangeExclusive() {
        "1...2" expandsLiteralTo "::Range.new(1, 2, true)"
    }

    fun testExpandProcPointerWithoutObject() {
        "->foo" expandsLiteralTo """
          -> do
            foo
          end
        """.trimIndent()
    }

    fun testExpandProcPointerWithParametersNoObject() {
        "->foo(Int32, String)" expandsLiteralTo """
          ->(__temp_1 : Int32, __temp_2 : String) do
            foo(__temp_1, __temp_2)
          end
        """.trimIndent()
    }

    fun testExpandGlobalProcLiteral() {
        "->::foo(Int32)" expandsLiteralTo """
          ->(__temp_1 : Int32) do
            ::foo(__temp_1)
          end
        """.trimIndent()
    }

    fun testExpandProcPointerWithConstReceiver() {
        "->Foo.foo(Int32)" expandsLiteralTo """
          ->(__temp_1 : Int32) do
            Foo.foo(__temp_1)
          end
        """.trimIndent()
    }

    fun testExpandGlobalProcPointerWithConstReceiver() {
        "->::Foo.foo(Int32)" expandsLiteralTo """
          ->(__temp_1 : Int32) do
            ::Foo.foo(__temp_1)
          end
        """.trimIndent()
    }

    fun testExpandProcPointerWithVariableReceiver() {
        assertExpressions("foo = 1; ->foo.bar(Int32)") {
            expressions[1] expandsLiteralTo """
              __temp_1 = foo
              ->(__temp_2 : Int32) do
                __temp_1.bar(__temp_2)
              end
            """.trimIndent()
        }
    }

    fun testExpandProcPointerWithInstanceVariableReceiver() {
        "->@foo.bar(Int32)" expandsLiteralTo """
          __temp_1 = @foo
          ->(__temp_2 : Int32) do
            __temp_1.bar(__temp_2)
          end
        """.trimIndent()
    }

    fun testExpandProcPointerWithClassVariableReceiver() {
        "->@@foo.bar(Int32)" expandsLiteralTo """
          __temp_1 = @@foo
          ->(__temp_2 : Int32) do
            __temp_1.bar(__temp_2)
          end
        """.trimIndent()
    }

    fun testExpandStringInterpolation() {
        "\"foo#{bar}baz\"" expandsLiteralTo "::String.interpolation(\"foo\", bar, \"baz\")"
    }

    fun testExpandStringInterpolationWithMultipleLines() {
        "\"foo\n#{bar}\nbaz\nqux\nfox\"" expandsLiteralTo "::String.interpolation(\"foo\\n\", bar, \"\\nbaz\\nqux\\nfox\")"
    }

    fun testExpandRegexEmpty() {
        "/#{\"\".to_s}/" expandsLiteralTo "::Regex.new(\"#{\"\".to_s}\", ::Regex::Options.new(0))"
    }

    fun testExpandRegexI() {
        "/#{\"\".to_s}/i" expandsLiteralTo "::Regex.new(\"#{\"\".to_s}\", ::Regex::Options.new(1))"
    }

    fun testExpandRegexX() {
        "/#{\"\".to_s}/x" expandsLiteralTo "::Regex.new(\"#{\"\".to_s}\", ::Regex::Options.new(8))"
    }

    fun testExpandRegexIM() {
        "/#{\"\".to_s}/im" expandsLiteralTo "::Regex.new(\"#{\"\".to_s}\", ::Regex::Options.new(7))"
    }

    fun testExpandRegexIMX() {
        "/#{\"\".to_s}/imx" expandsLiteralTo "::Regex.new(\"#{\"\".to_s}\", ::Regex::Options.new(15))"
    }

    fun testExpandRegexConst() {
        "/123/" expandsLiteralTo "\$Regex:1"
    }

    fun testExpandCaseWithCall() {
        "case x; when 1; 'b'; when 2; 'c'; else; 'd'; end" expandsLiteralTo  "__temp_1 = x\nif 1 === __temp_1\n  'b'\nelse\n  if 2 === __temp_1\n    'c'\n  else\n    'd'\n  end\nend"
    }

    fun testExpandCaseWithVarInCond() = assertExpressions("x = 1; case x; when 1; 'b'; end") {
        expressions[1] expandsLiteralTo "if 1 === x\n  'b'\nend"
    }

    fun testExpandCaseWithPathToIsA() = assertExpressions("x = 1; case x; when Foo; 'b'; end") {
        expressions[1] expandsLiteralTo "if x.is_a?(Foo)\n  'b'\nend"
    }

    fun testExpandCaseWithGenericToIsA() = assertExpressions("x = 1; case x; when Foo(T); 'b'; end") {
        expressions[1] expandsLiteralTo "if x.is_a?(Foo(T))\n  'b'\nend"
    }

    fun testExpandCaseWithPathDotClassToIsA() = assertExpressions("x = 1; case x; when Foo.class; 'b'; end") {
        expressions[1] expandsLiteralTo "if x.is_a?(Foo.class)\n  'b'\nend"
    }

    fun testExpandCaseWithGenericDotClassToIsA() = assertExpressions("x = 1; case x; when Foo(T).class; 'b'; end") {
        expressions[1] expandsLiteralTo "if x.is_a?(Foo(T).class)\n  'b'\nend"
    }

    fun testExpandCaseWithMultipleExprInWhen() = assertExpressions("x = 1; case x; when 1, 2; 'b'; end") {
        expressions[1] expandsLiteralTo "if (1 === x) || (2 === x)\n  'b'\nend"
    }

    fun testExpandCaseWithImplicitCall() {
        "case x; when .foo(1); 2; end" expandsLiteralTo "__temp_1 = x\nif __temp_1.foo(1)\n  2\nend"
    }

    fun testExpandCaseWithImplicitRespondsTo() {
        "case x; when .responds_to?(:foo); 2; end" expandsLiteralTo "__temp_1 = x\nif __temp_1.responds_to?(:foo)\n  2\nend"
    }

    fun testExpandCaseWithImplicitIsA() {
        "case x; when .is_a?(T); 2; end" expandsLiteralTo "__temp_1 = x\nif __temp_1.is_a?(T)\n  2\nend"
    }

    fun testExpandCaseWithImplicitAs() {
        "case x; when .as(T); 2; end" expandsLiteralTo "__temp_1 = x\nif __temp_1.as(T)\n  2\nend"
    }

    fun testExpandCaseWithImplicitAsQuestion() {
        "case x; when .as?(T); 2; end" expandsLiteralTo "__temp_1 = x\nif __temp_1.as?(T)\n  2\nend"
    }

    fun testExpandCaseWithImplicitNot() {
        "case x; when .!; 2; end" expandsLiteralTo "__temp_1 = x\nif !__temp_1\n  2\nend"
    }

    fun testExpandCaseWithAssignment() {
        "case x = 1; when 2; 3; end" expandsLiteralTo "x = 1\nif 2 === x\n  3\nend"
    }

    fun testExpandCaseWithAssignmentInParens() {
        "case (x = 1); when 2; 3; end" expandsLiteralTo "x = 1\nif 2 === x\n  3\nend"
    }

    fun testExpandCaseNoValue() {
        "case when 2; 3; when 4; 5; end" expandsLiteralTo "if 2\n  3\nelse\n  if 4\n    5\n  end\nend"
    }

    fun testExpandCaseWithMultipleExprInWhenNoValue() {
        "case when 2, 9; 3; when 4; 5; end" expandsLiteralTo "if 2 || 9\n  3\nelse\n  if 4\n    5\n  end\nend"
    }

    fun testExpandCaseWithNilToIsA() = assertExpressions("x = 1; case x; when nil; 'b'; end") {
        expressions[1] expandsLiteralTo "if x.is_a?(::Nil)\n  'b'\nend"
    }

    fun testExpandCaseWithMultipleExpr() = assertExpressions("x, y = 1, 2; case {x, y}; when {2, 3}; 4; end") {
        expressions[1] expandsLiteralTo "if (2 === x) && (3 === y)\n  4\nend"
    }

    fun testExpandCaseWithMultipleExprAndTypes() = assertExpressions("x, y = 1, 2; case {x, y}; when {Int32, Float64}; 4; end") {
        expressions[1] expandsLiteralTo "if x.is_a?(Int32) && y.is_a?(Float64)\n  4\nend"
    }

    fun testExpandCaseWithMultipleExprAndImplicitObj() = assertExpressions("x, y = 1, 2; case {x, y}; when {.foo, .bar}; 4; end") {
        expressions[1] expandsLiteralTo "if x.foo && y.bar\n  4\nend"
    }

    fun testExpandCaseWithMultipleExprAndComma() = assertExpressions("x, y = 1, 2; case {x, y}; when {2, 3}, {4, 5}; 6; end") {
        expressions[1] expandsLiteralTo "if ((2 === x) && (3 === y)) || ((4 === x) && (5 === y))\n  6\nend"
    }

    fun testExpandCaseWithMultipleExprAndUnderscore() = assertExpressions("x, y = 1, 2; case {x, y}; when {2, _}; 4; end") {
        expressions[1] expandsLiteralTo "if 2 === x\n  4\nend"
    }

    fun testExpandCaseWithMultipleExprAllUnderscores() = assertExpressions("x, y = 1, 2; case {x, y}; when {_, _}; 4; end") {
        expressions[1] expandsLiteralTo "if true\n  4\nend"
    }

    fun testExpandCaseWithMultipleExprAllUnderscoresTwice() = assertExpressions("x, y = 1, 2; case {x, y}; when {_, _}, {_, _}; 4; end") {
        expressions[1] expandsLiteralTo "if true\n  4\nend"
    }

    fun testExpandCaseWithMultipleExprAndNonTuple() = assertExpressions("x, y = 1, 2; case {x, y}; when 1; 4; end") {
        expressions[1] expandsLiteralTo "if 1 === {x, y}\n  4\nend"
    }

    fun testExpandCaseNoElseNoWhen() {
        "case x; end" expandsLiteralTo "x\nnil"
    }

    fun testExpandCaseWithElseNoWhen() {
        "case x; else; y; end" expandsLiteralTo "x\ny"
    }

    fun testExpandCaseNoCondNoElseNoWhen() {
        "case; end" expandsLiteralTo ""
    }

    fun testExpandCaseWithElseNoCondNoWhen() {
        "case; else; y; end" expandsLiteralTo "y"
    }

    fun testExpandCaseWithPathDotClassToIsAIn() = assertExpressions("x = 1; case x; in Foo.class; 'b'; end") {
        expressions[1] expandsLiteralTo "if x.is_a?(Foo.class)\n  'b'\nelse\n  raise \"unreachable\"\nend"
    }

    fun testExpandSelectWithCall() {
        "select; when foo; body; when bar; baz; end" expandsLiteralTo """
          __temp_1, __temp_2 = ::Channel.select({foo_select_action, bar_select_action})
          case __temp_1
          when 0
            body
          when 1
            baz
          else
            ::raise("BUG: invalid select index")
          end
        """.trimIndent()
    }

    fun testExpandSelectWithAssign() {
        "select; when x = foo; x + 1; end" expandsLiteralTo """
          __temp_1, __temp_2 = ::Channel.select({foo_select_action})
          case __temp_1
          when 0
            x = __temp_2.as(typeof(foo))
            x + 1
          else
            ::raise("BUG: invalid select index")
          end
        """.trimIndent()
    }

    fun testExpandSelectWithElse() {
        "select; when foo; body; else; baz; end" expandsLiteralTo """
          __temp_1, __temp_2 = ::Channel.non_blocking_select({foo_select_action})
          case __temp_1
          when 0
            body
          else
            baz
          end
        """.trimIndent()
    }

    fun testExpandSelectWithAssignAndQuestionMethod() {
        "select; when x = foo?; x + 1; end" expandsLiteralTo """
          __temp_1, __temp_2 = ::Channel.select({foo_select_action?})
          case __temp_1
          when 0
            x = __temp_2.as(typeof(foo?))
            x + 1
          else
            ::raise("BUG: invalid select index")
          end
        """.trimIndent()
    }

    fun testExpandSelectWithAssignAndBangMethod() {
        "select; when x = foo!; x + 1; end" expandsLiteralTo """
          __temp_1, __temp_2 = ::Channel.select({foo_select_action!})
          case __temp_1
          when 0
            x = __temp_2.as(typeof(foo!))
            x + 1
          else
            ::raise("BUG: invalid select index")
          end
        """.trimIndent()
    }

    fun testExpandArrayLiteralEmptyWithOf() {
        "[] of Int" expandsLiteralTo "::Array(Int).new"
    }

    fun testExpandArrayLiteralNonEmptyWithOf() {
        "[1, 2] of Int8" expandsLiteralTo """
          __temp_1 = ::Array(Int8).unsafe_build(2)
          __temp_2 = __temp_1.to_unsafe
          __temp_2[0] = 1
          __temp_2[1] = 2
          __temp_1
        """.trimIndent()
    }

    fun testExpandArrayLiteralNonEmptyNoOf() {
        "[1, 2]" expandsLiteralTo """
          __temp_1 = ::Array(typeof(1, 2)).unsafe_build(2)
          __temp_2 = __temp_1.to_unsafe
          __temp_2[0] = 1
          __temp_2[1] = 2
          __temp_1
        """.trimIndent()
    }

    fun testExpandArrayLiteralNonEmptyWithOfAndSplat() {
        "[1, *2, *3, 4, 5] of Int8" expandsLiteralTo """
          __temp_1 = ::Array(Int8).new(3)
          __temp_1 << 1
          __temp_1.concat(2)
          __temp_1.concat(3)
          __temp_1 << 4
          __temp_1 << 5
          __temp_1
        """.trimIndent()
    }

    fun testExpandArrayLiteralNonEmptyNoOfWithSplat() {
        "[1, *2, *3, 4, 5]" expandsLiteralTo """
          __temp_1 = ::Array(typeof(1, ::Enumerable.element_type(2), ::Enumerable.element_type(3), 4, 5)).new(3)
          __temp_1 << 1
          __temp_1.concat(2)
          __temp_1.concat(3)
          __temp_1 << 4
          __temp_1 << 5
          __temp_1
        """.trimIndent()
    }

    fun testExpandArrayLiteralNonEmptyNoOfSplatOnly() {
        "[*1]" expandsLiteralTo """
          __temp_1 = ::Array(typeof(::Enumerable.element_type(1))).new(0)
          __temp_1.concat(1)
          __temp_1
        """.trimIndent()
    }

    fun testExpandArrayLiteralHoistsComplexElementExpr() {
        "[[1]]" expandsLiteralTo """
          __temp_1 = [1]
          __temp_2 = ::Array(typeof(__temp_1)).unsafe_build(1)
          __temp_3 = __temp_2.to_unsafe
          __temp_3[0] = __temp_1
          __temp_2
        """.trimIndent()
    }

    fun testExpandArrayLiteralHoistsComplexElementExprWithSplat() {
        "[*[1]]" expandsLiteralTo """
          __temp_1 = [1]
          __temp_2 = ::Array(typeof(::Enumerable.element_type(__temp_1))).new(0)
          __temp_2.concat(__temp_1)
          __temp_2
        """.trimIndent()
    }

    fun testExpandArrayLiteralHoistsComplexElementExprArrayLike() {
        "Foo{[1], *[2]}" expandsLiteralNamedTo """
          __temp_1 = [1]
          __temp_2 = [2]
          __temp_3 = Foo.new
          __temp_3 << __temp_1
          __temp_2.each do |__temp_4|
            __temp_3 << __temp_4
          end
          __temp_3
        """.trimIndent()
    }

    fun testExpandArrayLiteralHoistsComplexElementExprArrayLikeGeneric() {
        "Foo{[1], *[2]}".expandsLiteralNamedTo(
            """
              __temp_1 = [1]
              __temp_2 = [2]
              __temp_3 = Foo(typeof(__temp_1, ::Enumerable.element_type(__temp_2))).new
              __temp_3 << __temp_1
              __temp_2.each do |__temp_4|
                __temp_3 << __temp_4
              end
              __temp_3
            """.trimIndent(),
            "Foo".path
        )
    }

    fun testExpandHashLiteralEmptyWithOf() {
        "{} of Int => Float" expandsLiteralTo "::Hash(Int, Float).new"
    }

    fun testExpandHashLiteralNonEmptyWithOf() {
        "{1 => 2, 3 => 4} of Int => Float" expandsLiteralTo """
          __temp_1 = ::Hash(Int, Float).new
          __temp_1[1] = 2
          __temp_1[3] = 4
          __temp_1
        """.trimIndent()
    }

    fun testExpandHashLiteralNonEmptyNoOf() {
        "{1 => 2, 3 => 4}" expandsLiteralTo """
          __temp_1 = ::Hash(typeof(1, 3), typeof(2, 4)).new
          __temp_1[1] = 2
          __temp_1[3] = 4
          __temp_1
        """.trimIndent()
    }

    fun testExpandHashLiteralHoistsComplexElementExpr() {
        "{[1] => 2, 3 => [4]}" expandsLiteralTo """
          __temp_1 = [1]
          __temp_2 = [4]
          __temp_3 = ::Hash(typeof(__temp_1, 3), typeof(2, __temp_2)).new
          __temp_3[__temp_1] = 2
          __temp_3[3] = __temp_2
          __temp_3
        """.trimIndent()
    }

    fun testExpandHashLiteralHoistsComplexElementExprHashLike() {
        "Foo{[1] => 2, 3 => [4]}" expandsLiteralNamedTo """
          __temp_1 = [1]
          __temp_2 = [4]
          __temp_3 = Foo.new
          __temp_3[__temp_1] = 2
          __temp_3[3] = __temp_2
          __temp_3
        """.trimIndent()
    }

    fun testExpandHashLiteralHoistsComplexElementExprHashLikeGeneric() {
        "Foo{[1] => 2, 3 => [4]}".expandsLiteralNamedTo(
            """
              __temp_1 = [1]
              __temp_2 = [4]
              __temp_3 = Foo(typeof(__temp_1, 3), typeof(2, __temp_2)).new
              __temp_3[__temp_1] = 2
              __temp_3[3] = __temp_2
              __temp_3
            """.trimIndent(),
            "Foo".path
        )
    }

    fun testExpandMultiAssignNToN() {
        "a, b, c = 1, 2, 3" expandsLiteralTo """
          __temp_1 = 1
          __temp_2 = 2
          __temp_3 = 3
          a = __temp_1
          b = __temp_2
          c = __temp_3
        """.trimIndent()
    }

    fun testExpandMultiAssignNToNWithIndexing() = assertExpressions("a = 1; b = 2; a[0], b[1] = 2, 3") {
        expressions[2] expandsLiteralTo """
          __temp_1 = 2
          __temp_2 = 3
          a[0] = __temp_1
          b[1] = __temp_2
        """.trimIndent()
    }

    fun testExpandMultiAssignNToNWithCall() = assertExpressions("a = 1; b = 2; a.foo, b.bar = 2, 3") {
        expressions[2] expandsLiteralTo """
          __temp_1 = 2
          __temp_2 = 3
          a.foo = __temp_1
          b.bar = __temp_2
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToNNotStrict() = assertExpressions("d = 1; a, b, c = d") {
        expressions[1] expandsLiteralTo """
          __temp_1 = d
          a = __temp_1[0]
          b = __temp_1[1]
          c = __temp_1[2]
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToNWithIndexingNotStrict() = assertExpressions("a = 1; b = 2; a[0], b[1] = 2") {
        expressions[2] expandsLiteralTo """
          __temp_1 = 2
          a[0] = __temp_1[0]
          b[1] = __temp_1[1]
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToNWithCallNotStrict() = assertExpressions("a = 1; b = 2; a.foo, b.bar = 2") {
        expressions[2] expandsLiteralTo """
          __temp_1 = 2
          a.foo = __temp_1[0]
          b.bar = __temp_1[1]
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToNStrict() = project.withFlag(CrystalFlags.STRICT_MULTI_ASSIGN) {
        assertExpressions("d = 1; a, b, c = d") {
            expressions[1] expandsLiteralTo """
              __temp_1 = d
              if __temp_1.size != 3
                ::raise(::IndexError.new("Multiple assignment count mismatch"))
              end
              a = __temp_1[0]
              b = __temp_1[1]
              c = __temp_1[2]
            """.trimIndent()
        }
    }

    fun testExpandMultiAssign1ToNWithIndexingStrict() = project.withFlag(CrystalFlags.STRICT_MULTI_ASSIGN) {
        assertExpressions("a = 1; b = 2; a[0], b[1] = 2") {
            expressions[2] expandsLiteralTo """
              __temp_1 = 2
              if __temp_1.size != 2
                ::raise(::IndexError.new("Multiple assignment count mismatch"))
              end
              a[0] = __temp_1[0]
              b[1] = __temp_1[1]
            """.trimIndent()
        }
    }

    fun testExpandMultiAssign1ToNWithCallStrict() = project.withFlag(CrystalFlags.STRICT_MULTI_ASSIGN) {
        assertExpressions("d = 1; a, b, c = d") {
            expressions[1] expandsLiteralTo """
              __temp_1 = d
              if __temp_1.size != 3
                ::raise(::IndexError.new("Multiple assignment count mismatch"))
              end
              a = __temp_1[0]
              b = __temp_1[1]
              c = __temp_1[2]
            """.trimIndent()
        }
    }

    fun testExpandMultiAssignMToNEmptySplat() = assertExpressions("a = 1; b = 2; *a[0], b.foo, c = 3, 4") {
        expressions[2] expandsLiteralTo """
          __temp_1 = ::Tuple.new
          __temp_2 = 3
          __temp_3 = 4
          a[0] = __temp_1
          b.foo = __temp_2
          c = __temp_3
        """.trimIndent()
    }

    fun testExpandMultiAssignMToNNonEmptySplat() = assertExpressions("a = 1; b = 2; a[0], *b.foo, c = 3, 4, 5, 6, 7") {
        expressions[2] expandsLiteralTo """
          __temp_1 = 3
          __temp_2 = ::Tuple.new(4, 5, 6)
          __temp_3 = 7
          a[0] = __temp_1
          b.foo = __temp_2
          c = __temp_3
        """.trimIndent()
    }

    fun testExpandMultiAssignMToNUnderscoreSplat1() {
        "a, *_, b, c = 1, 2, 3, 4, 5" expandsLiteralTo """
          __temp_1 = 1
          2
          3
          __temp_2 = 4
          __temp_3 = 5
          a = __temp_1
          b = __temp_2
          c = __temp_3
        """.trimIndent()
    }

    fun testExpandMultiAssignMToNUnderscoreSplat2() {
        "*_, a, b, c = 1, 2, 3, 4, 5" expandsLiteralTo """
          1
          2
          __temp_1 = 3
          __temp_2 = 4
          __temp_3 = 5
          a = __temp_1
          b = __temp_2
          c = __temp_3
        """.trimIndent()
    }

    fun testExpandMultiAssignMToNUnderscoreSplat3() {
        "a, b, c, *_ = 1, 2, 3, 4, 5" expandsLiteralTo """
          __temp_1 = 1
          __temp_2 = 2
          __temp_3 = 3
          4
          5
          a = __temp_1
          b = __temp_2
          c = __temp_3
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToNSplat() = assertExpressions("c = 1; d = 2; a, b, *c.foo, d[0], e, f = 3") {
        expressions[2] expandsLiteralTo """
          __temp_1 = 3
          if __temp_1.size < 5
            ::raise(::IndexError.new("Multiple assignment count mismatch"))
          end
          a = __temp_1[0]
          b = __temp_1[1]
          c.foo = __temp_1[2..-4]
          d[0] = __temp_1[-3]
          e = __temp_1[-2]
          f = __temp_1[-1]
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToNSplatBeforeOtherTargets() {
        "*a, b, c, d = 3" expandsLiteralTo """
          __temp_1 = 3
          a = __temp_1[0..-4]
          b = __temp_1[-3]
          c = __temp_1[-2]
          d = __temp_1[-1]
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToNSplatAfterOtherTargets() {
        "a, b, c, *d = 3" expandsLiteralTo """
          __temp_1 = 3
          a = __temp_1[0]
          b = __temp_1[1]
          c = __temp_1[2]
          d = __temp_1[3..-1]
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToNSplatUnderscore1() {
        "a, *_, b, c = 1" expandsLiteralTo """
          __temp_1 = 1
          if __temp_1.size < 3
            ::raise(::IndexError.new("Multiple assignment count mismatch"))
          end
          a = __temp_1[0]
          b = __temp_1[-2]
          c = __temp_1[-1]
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToNSplatUnderscore2() {
        "*_, a, b, c = 1" expandsLiteralTo """
          __temp_1 = 1
          a = __temp_1[-3]
          b = __temp_1[-2]
          c = __temp_1[-1]
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToNSplatUnderscore3() {
        "a, b, c, *_ = 1" expandsLiteralTo """
          __temp_1 = 1
          a = __temp_1[0]
          b = __temp_1[1]
          c = __temp_1[2]
        """.trimIndent()
    }

    fun testExpandMultiAssignNToSplat() {
        "*a = 1, 2, 3, 4" expandsLiteralTo """
          __temp_1 = ::Tuple.new(1, 2, 3, 4)
          a = __temp_1
        """.trimIndent()
    }

    fun testExpandMultiAssignNToSplatUnderscore() {
        "*_ = 1, 2, 3, 4" expandsLiteralTo """
          1
          2
          3
          4
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToSplat() {
        "*a = 1" expandsLiteralTo """
          __temp_1 = 1
          a = __temp_1[0..-1]
        """.trimIndent()
    }

    fun testExpandMultiAssign1ToSplatUnderscore() {
        "*_ = 1" expandsLiteralTo """
          __temp_1 = 1
        """.trimIndent()
    }
}
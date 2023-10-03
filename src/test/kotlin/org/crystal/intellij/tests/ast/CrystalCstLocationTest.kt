package org.crystal.intellij.tests.ast

import com.intellij.openapi.util.text.StringUtil
import junit.framework.TestCase
import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.nodes.*
import org.crystal.intellij.util.cast

class CrystalCstLocationTest : CrystalCstParsingTestBase() {
    private fun String.sourceBetween(location: CstLocation): String {
        return substring(location.startOffset, location.endOffset)
    }

    private fun String.nodeSource(node: CstNode<*>): String {
        return sourceBetween(node.location!!)
    }

    private fun String.nameSource(node: CstNode<*>): String {
        return sourceBetween(node.nameLocation!!)
    }

    private fun assertEndLocationAndText(
        source: String,
        line: Int = 1,
        col: Int = source.length
    ) {
        val text = "$source; 1"
        assertExpressions(text) {
            val node = expressions[0]
            TestCase.assertEquals(source, text.nodeSource(node))
            node.assertEnd(line, col)
        }
    }

    private fun CstNode<*>.assertStart(line: Int, col: Int) {
        val loc = location!!
        TestCase.assertEquals(line, loc.startLine)
        TestCase.assertEquals(col, loc.startColumn)
    }

    private fun CstNode<*>.assertEnd(line: Int, col: Int) {
        val loc = location!!
        TestCase.assertEquals(line, loc.endLine)
        TestCase.assertEquals(col, loc.endColumn)
    }

    private fun CstNode<*>.assertNoEnd() {
        val loc = location ?: return
        TestCase.assertEquals(-1, loc.endOffset)
    }

    fun testEndLocations() {
        assertEndLocationAndText("nil")
        assertEndLocationAndText("false")
        assertEndLocationAndText("123")
        assertEndLocationAndText("123.45")
        assertEndLocationAndText("'a'")
        assertEndLocationAndText(":foo")
        assertEndLocationAndText("\"hello\"")
        assertEndLocationAndText("[1, 2]")
        assertEndLocationAndText("[] of Int32")
        assertEndLocationAndText("{a: 1}")
        assertEndLocationAndText("{} of Int32 => String")
        assertEndLocationAndText("1..3")
        assertEndLocationAndText("/foo/")
        assertEndLocationAndText("{1, 2}")
        assertEndLocationAndText("foo")
        assertEndLocationAndText("foo(1, 2)")
        assertEndLocationAndText("foo 1, 2")
        assertEndLocationAndText("Foo")
        assertEndLocationAndText("Foo(A)")
        assertEndLocationAndText("if 1; else; 2; end")
        assertEndLocationAndText("if 1; elseif; 2; end")
        assertEndLocationAndText("unless 1; 2; end")
        assertEndLocationAndText("a = 123")
        assertEndLocationAndText("a, b = 1, 2")
        assertEndLocationAndText("@foo")
        assertEndLocationAndText("foo.@foo")
        assertEndLocationAndText("@@foo")
        assertEndLocationAndText("a && b")
        assertEndLocationAndText("a || b")
        assertEndLocationAndText("def foo; end")
        assertEndLocationAndText("def foo; 1; end")
        assertEndLocationAndText("def foo; rescue ex; end")
        assertEndLocationAndText("abstract def foo")
        assertEndLocationAndText("abstract def foo : Int32")
        assertEndLocationAndText("begin; 1; end")
        assertEndLocationAndText("class Foo; end")
        assertEndLocationAndText("struct Foo; end")
        assertEndLocationAndText("module Foo; end")
        assertEndLocationAndText("->{ }")
        assertEndLocationAndText("macro foo;end")
        assertEndLocationAndText("macro foo; 123; end")
        assertEndLocationAndText("!foo")
        assertEndLocationAndText("pointerof(@foo)")
        assertEndLocationAndText("sizeof(Foo)")
        assertEndLocationAndText("offsetof(Foo, @a)")
        assertEndLocationAndText("offsetof({X, Y}, 1)")
        assertEndLocationAndText("typeof(1)")
        assertEndLocationAndText("1 if 2")
        assertEndLocationAndText("while 1; end")
        assertEndLocationAndText("return")
        assertEndLocationAndText("return 1")
        assertEndLocationAndText("yield")
        assertEndLocationAndText("yield 1")
        assertEndLocationAndText("include Foo")
        assertEndLocationAndText("extend Foo")
        assertEndLocationAndText("1.as(Int32)")
        assertEndLocationAndText("puts obj.foo")
        assertEndLocationAndText("a, b = 1, 2 if 3")
        assertEndLocationAndText("abstract def foo(x)")
        assertEndLocationAndText("::foo")
        assertEndLocationAndText("foo.[0] = 1")
        assertEndLocationAndText("x : Foo(A, *B, C)")
        assertEndLocationAndText("Int[8]?")
        assertEndLocationAndText("[1, 2,]")
        assertEndLocationAndText("foo(\n  &.block\n)", line = 3, col = 1)
        assertEndLocationAndText("foo.bar(x) do; end")
        assertEndLocationAndText("%w(one two)")
        assertEndLocationAndText("{%\nif foo\n  bar\n end\n%}", line = 5, col = 2)
        assertEndLocationAndText("foo bar, out baz")
        assertEndLocationAndText("Foo?")
        assertEndLocationAndText("foo : Foo.class")
        assertEndLocationAndText("foo : Foo?")
        assertEndLocationAndText("foo : Foo*")
        assertEndLocationAndText("foo : Foo**")
        assertEndLocationAndText("foo : Foo[42]")
        assertEndLocationAndText("foo ->bar")
        assertEndLocationAndText("foo ->bar=")
        assertEndLocationAndText("foo ->self.bar")
        assertEndLocationAndText("foo ->self.bar=")
        assertEndLocationAndText("foo ->Bar.baz")
        assertEndLocationAndText("foo ->Bar.baz=")
        assertEndLocationAndText("foo ->@bar.baz")
        assertEndLocationAndText("foo ->@bar.baz=")
        assertEndLocationAndText("foo ->@@bar.baz")
        assertEndLocationAndText("foo ->@@bar.baz=")
        assertEndLocationAndText("foo ->bar(Baz)")
        assertEndLocationAndText("foo *bar")
        assertEndLocationAndText("foo **bar")
        assertEndLocationAndText("Foo { 1 }")
        assertEndLocationAndText("foo.!")
        assertEndLocationAndText("foo.!()")
        assertEndLocationAndText("f.x = foo")
        assertEndLocationAndText("f.x=(*foo)")
        assertEndLocationAndText("f.x=(foo).bar")
        assertEndLocationAndText("x : Foo ->")
        assertEndLocationAndText("x : Foo -> Bar")
        assertEndLocationAndText("require \"foo\"")
        assertEndLocationAndText("begin; 1; 2; 3; end")
        assertEndLocationAndText("1..")
        assertEndLocationAndText("foo.responds_to?(:foo)")
        assertEndLocationAndText("foo.responds_to? :foo")
        assertEndLocationAndText("foo.nil?")
        assertEndLocationAndText("foo.nil?(  )")
        assertEndLocationAndText("@a = uninitialized Foo")
        assertEndLocationAndText("@@a = uninitialized Foo")
        assertEndLocationAndText("1 rescue 2")
        assertEndLocationAndText("1 ensure 2")
        assertEndLocationAndText("foo.bar= *baz")
    }

    fun testTildeLoc() = assertNode<CstCall>("\n  ~1") {
        assertStart(2, 3)
    }

    fun testVarEndLoc() = assertExpressions("foo = 1\nfoo; 1") {
        expressions[1].assertEnd(2, 3)
    }

    fun testVarPlusVarEndLoc() = assertExpressions("foo = 1\nfoo + nfoo; 1") {
        expressions[1].cast<CstCall>().obj.cast<CstVar>().assertEnd(2, 3)
    }

    fun testCurlyBlockEndLoc() = assertExpressions("foo { 1 + 2 }; 1") {
        val node = expressions[0].cast<CstCall>()
        val block = node.block!!
        block.assertEnd(1, 13)
        node.assertEnd(1, 13)
    }

    fun testDoBlockEndLoc() = assertExpressions("foo do\n  1 + 2\nend; 1") {
        val node = expressions[0].cast<CstCall>()
        val block = node.block!!
        block.assertEnd(3, 3)
        node.assertEnd(3, 3)
    }

    fun testLocAfterMacroWithYield() = assertExpressions("\nmacro foo\n  yield\nend\n\n1 + 'a'") {
        expressions[1].assertStart(6, 1)
    }

    fun testNewlineLoc() = assertExpressions(StringUtil.convertLineSeparators("class Foo\r\nend\r\n\r\n1")) {
        expressions.last().assertStart(4, 1)
    }

    fun testEnumMethodLoc() = assertNode<CstEnumDef>("enum Foo; A; def bar; end; end") {
        members[1].assertStart(1, 14)
    }

    fun testConditionalExprLoc() = assertNode<CstConditionalExpression<*>>("\n  1 ? 2 : 3") {
        assertStart(2, 3)
    }

    fun testEmptyExceptionHandlerLocInDef() = assertNode<CstDef>("def foo\nensure\nend") {
        body.assertStart(2, 1)
    }

    fun testProcLiteralLoc() = assertNode<CstProcLiteral>("->(\n  x : Int32,\n  y : String\n) { }") {
        assertStart(1, 1)
    }

    fun testIfElseLoc() = assertNode<CstIf>("if foo\nelse\nend") {
        assertStart(1, 1)
        assertEnd(3, 3)
    }

    fun testIfLoc() = assertNode<CstIf>("if foo\nend") {
        assertStart(1, 1)
        assertEnd(2, 3)
    }

    fun testElsifLoc() = assertNode<CstIf>("if foo\nelsif bar\nend") {
        val node = elseBranch as CstIf
        node.assertStart(2, 1)
        node.assertEnd(3, 3)
    }

    fun testUnlessElseLoc() = assertNode<CstUnless>("unless foo\nelse\nend") {
        assertStart(1, 1)
        assertEnd(3, 3)
    }

    fun testBeginEndBlockLoc() = assertNode<CstExpressions>("begin\nfoo\nend") {
        assertStart(1, 1)
        assertEnd(3, 3)
    }

    fun testEmptyParenthesizedBlockLoc() = assertExpressions("()") {
        assertStart(1, 1)
        assertEnd(1, 2)
    }

    fun testParenthesizedBlockLoc() = assertExpressions("(foo; bar)") {
        assertStart(1, 1)
        assertEnd(1, 10)
    }

    fun testExceptionHandlerEmptyLoc() = assertNode<CstExceptionHandler>("begin\nrescue\nelse\nensure\nend") {
        assertStart(1, 1)
        assertEnd(5, 3)
        rescues.first().assertStart(2, 1)
        rescues.first().assertNoEnd()
        TestCase.assertEquals(CstNop, elseBranch)
        TestCase.assertEquals(CstNop, ensure)
    }

    fun testExceptionHandlerLoc() = assertNode<CstExceptionHandler>("begin\nrescue\n1\nelse\n2\nensure\n3\nend") {
        assertStart(1, 1)
        assertEnd(8, 3)
        rescues.first().assertStart(2, 1)
        rescues.first().assertEnd(3, 1)
        elseBranch!!.assertStart(5, 1)
        elseBranch!!.assertEnd(5, 1)
        ensure!!.assertStart(7, 1)
        ensure!!.assertEnd(7, 1)
    }

    fun testTrailingEnsureLoc() = assertNode<CstExceptionHandler>("foo ensure bar") { source ->
        ensure!!.assertStart(1, 12)
        ensure!!.assertEnd(1, 14)
        TestCase.assertEquals("bar", source.nodeSource(ensure!!))
    }

    fun testTrailingRescueLoc() = assertNode<CstExceptionHandler>("foo rescue bar") { source ->
        val rescue = rescues.first()
        rescue.assertStart(1, 5)
        rescue.assertEnd(1, 14)
        TestCase.assertEquals("rescue bar", source.nodeSource(rescue))
    }

    fun testArrayLiteralElementLoc() = assertNode<CstArrayLiteral>("%i(foo bar)") { source ->
        TestCase.assertEquals("foo", source.nodeSource(elements[0]))
        TestCase.assertEquals("bar", source.nodeSource(elements[1]))
    }

    fun testImplicitReturnTupleElementLoc() = assertNode<CstDef>("def foo; return 1, 2; end") { source ->
        val node = body.cast<CstReturn>().expression!!
        TestCase.assertEquals("1, 2", source.nodeSource(node))
    }

    fun testVarLocInTypeDeclaration() = assertNode<CstTypeDeclaration>("foo : Int32") { source ->
        TestCase.assertEquals("foo", source.nodeSource(variable))
    }

    fun testKeywordVarLocInTypeDeclaration() = assertNode<CstTypeDeclaration>("begin : Int32") { source ->
        TestCase.assertEquals("begin", source.nodeSource(variable))
    }

    fun testVarLocInProcPointer() = assertExpressions("foo : Foo; ->foo.bar") { source ->
        val node = expressions[1].cast<CstProcPointer>().obj!!
        TestCase.assertEquals("foo", source.nodeSource(node))
    }

    fun testVarLocInMacroFor() = assertNode<CstMacroFor>("{% for foo, bar in baz %} {% end %}") { source ->
        TestCase.assertEquals("foo", source.nodeSource(vars[0]))
        TestCase.assertEquals("bar", source.nodeSource(vars[1]))
    }

    fun testReceiverVarLocInDef() = assertNode<CstDef>("def foo.bar; end") { source ->
        TestCase.assertEquals("foo", source.nodeSource(receiver!!))
    }

    fun testVarLocInCStruct() = assertNode<CstLibDef>("lib Foo; struct Bar; fizz, buzz : Int32; end; end") { source ->
        val expressions = body.cast<CstCStructOrUnionDef>().body.cast<CstExpressions>().expressions
        TestCase.assertEquals("fizz", source.nodeSource(expressions[0].cast<CstTypeDeclaration>().variable))
        TestCase.assertEquals("buzz", source.nodeSource(expressions[1].cast<CstTypeDeclaration>().variable))
    }

    fun testLineNumberAfterEscapedMacroExpStart() = assertExpressions(
        """
        macro foo
          \\{%
            1
          %}
        end

        1
        """.trimIndent()
    ) {
        expressions[1].assertStart(7, 1)
    }

    fun testParamLocInProcLiteral() = assertNode<CstProcLiteral>("->(foo : Bar, baz) { }") { source ->
        val args = def.args
        TestCase.assertEquals("foo : Bar", source.nodeSource(args[0]))
        TestCase.assertEquals("baz", source.nodeSource(args[1]))
    }

    fun testSplatLocInMultiAssign1() = assertNode<CstMultiAssign>("*foo, bar = 1, 2") { source ->
        TestCase.assertEquals("*foo", source.nodeSource(targets[0]))
    }

    fun testSplatLocInMultiAssign2() = assertNode<CstMultiAssign>("foo, *bar = 1, 2") { source ->
        TestCase.assertEquals("*bar", source.nodeSource(targets[1]))
    }

    fun testTupleTypeLoc() = assertNode<CstTypeDeclaration>("x : {Foo, Bar}") { source ->
        TestCase.assertEquals("{Foo, Bar}", source.nodeSource(type))
    }

    fun testNamedTupleTypeLoc() = assertNode<CstTypeDeclaration>("x : {foo: Bar}") { source ->
        TestCase.assertEquals("{foo: Bar}", source.nodeSource(type))
    }

    fun testNamedTupleTypeArgLoc() = assertNode<CstTypeDeclaration>("x : {foo: Bar}") { source ->
        val node = type.cast<CstGeneric>().namedArgs.first()
        TestCase.assertEquals("foo: Bar", source.nodeSource(node))
    }

    fun testInstanceVarLocInProcPointer() = assertNode<CstProcPointer>("->@foo.x") { source ->
        TestCase.assertEquals("@foo", source.nodeSource(obj!!))
    }

    fun testClassVarLocInProcPointer() = assertNode<CstProcPointer>("->@@foo.x") { source ->
        TestCase.assertEquals("@@foo", source.nodeSource(obj!!))
    }

    fun testAnnotationLocOnMethodParam() = assertNode<CstDef>("def x(@[Foo] y) end") { source ->
        val node = args.first().annotations.first()
        TestCase.assertEquals("@[Foo]", source.nodeSource(node))
    }

    fun testAnnotationLocInLib() = assertNode<CstLibDef>("lib X; @[Foo]; end") { source ->
        TestCase.assertEquals("@[Foo]", source.nodeSource(body))
    }

    fun testAnnotationLocInEnum() = assertNode<CstEnumDef>("enum X; @[Foo]; end") { source ->
        TestCase.assertEquals("@[Foo]", source.nodeSource(members.first()))
    }

    fun testPrivateMethodLocInEnum() = assertNode<CstEnumDef>("enum X; private def foo; end; end") { source ->
        TestCase.assertEquals("private def foo; end", source.nodeSource(members.first()))
    }

    fun testProtectedMethodLocInEnum() = assertNode<CstEnumDef>("enum X; protected def foo; end; end") { source ->
        TestCase.assertEquals("protected def foo; end", source.nodeSource(members.first()))
    }

    fun testPlusAssignLoc() = assertExpressions("a = 1; a += 2") {
        expressions[1].assertNameStart(1, 10)
    }

    fun testPlusAssignLocInQualifiedCall() = assertExpressions("a = 1; a.x += 2") {
        expressions[1].assertNameStart(1, 12)
    }

    fun testCallNameLoc() = assertNode<CstCall>("foo(bar)") { source ->
        TestCase.assertEquals("foo", source.nameSource(this))
    }

    fun testCallNameLocInOpAssign() = assertNode<CstOpAssign>("@foo.bar += 1") { source ->
        TestCase.assertEquals("bar", source.nameSource(target as CstCall))
    }
}
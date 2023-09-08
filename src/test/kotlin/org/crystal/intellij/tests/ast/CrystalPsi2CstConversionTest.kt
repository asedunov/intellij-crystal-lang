package org.crystal.intellij.tests.ast

import com.intellij.openapi.util.text.StringUtil
import com.intellij.testFramework.ParsingTestCase
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import junit.framework.TestCase
import org.crystal.intellij.lang.ast.cstNode
import org.crystal.intellij.lang.ast.nodes.*
import org.crystal.intellij.lang.config.CrystalLevel
import org.crystal.intellij.lang.config.CrystalProjectSettings
import org.crystal.intellij.lang.parser.CrystalParserDefinition
import org.crystal.intellij.lang.psi.CrFile
import org.crystal.intellij.lang.psi.CrVisibility
import org.crystal.intellij.lang.resolve.CrResolveFacade
import org.crystal.intellij.tests.util.withLanguageLevel

class CrystalPsi2CstConversionTest : ParsingTestCase("parser", "cr", CrystalParserDefinition()) {
    override fun getTestDataPath() = ""

    override fun setUp() {
        super.setUp()

        project.registerService(CrystalProjectSettings::class.java)
        project.registerService(CrResolveFacade::class.java)
    }

    private infix fun String.becomes(node: CstNode) {
        myFile = createPsiFile("a", this)
        ensureParsed(myFile)
        TestCase.assertEquals(node, (myFile as CrFile).cstNode)
    }

    fun testConversion() {
        "nil" becomes nilLiteral

        "true" becomes true.bool
        "false" becomes false.bool

        "1" becomes 1.int32
        "+1" becomes 1.int32
        "-1" becomes (-1).int32

        "1_i64" becomes 1.int64
        "+1_i64" becomes 1.int64
        "-1_i64" becomes (-1).int64

        "1_u128" becomes 1.uint128
        "1_i128" becomes 1.int128

        "1.0" becomes 1.0.float64
        "+1.0" becomes 1.0.float64
        "-1.0" becomes (-1.0).float64

        "1.0_f32" becomes 1.0.float32
        "+1.0_f32" becomes 1.0.float32
        "-1.0_f32" becomes (-1.0).float32

        "2.3_f32" becomes 2.3.float32

        "'a'" becomes 'a'.char

        "\"foo\"" becomes "foo".string
        "\"\"" becomes "".string
        "\"hello \\\n     world\"" becomes "hello world".string
        "\"hello \\\r\n     world\"" becomes "hello world".string

        "%Q{hello \\n world}" becomes "hello \n world".string
        "%q{hello \\n world}" becomes "hello \\n world".string
        "%q{hello \\#{foo} world}" becomes "hello \\#{foo} world".string

        listOf(":foo", ":foo!", ":foo?", ":\"foo\"", ":かたな", ":+", ":-", ":*", ":/", ":==", ":<", ":<=", ":>",
            ":>=", ":!", ":!=", ":=~", ":!~", ":&", ":|", ":^", ":~", ":**", ":&**", ":>>", ":<<", ":%", ":[]", ":[]?",
            ":[]=", ":<=>", ":===").forEach { symbol ->
            symbol becomes StringUtil.unquoteString(symbol.substring(1)).symbol
        }
        ":foo" becomes "foo".symbol
        ":[]=" becomes "[]=".symbol
        ":[]?" becomes "[]?".symbol
        ":\"\\\\foo\"" becomes "\\foo".symbol
        ":\"\\\"foo\"" becomes "\"foo".symbol
        ":\"\\\"foo\\\"\"" becomes "\"foo\"".symbol
        ":\"\\a\\b\\n\\r\\t\\v\\f\\e\"" becomes "\u0007\b\n\r\t\u000b\u000c\u001b".symbol
        ":\"\\u{61}\"" becomes "a".symbol
        ":\"\"" becomes "".symbol

        "[1, 2]" becomes listOf(1.int32, 2.int32).array
        "[\n1, 2]" becomes listOf(1.int32, 2.int32).array
        "[1,\n 2,]" becomes listOf(1.int32, 2.int32).array

        "1 + 2" becomes CstCall(1.int32, "+", 2.int32)
        "1 +\n2" becomes CstCall(1.int32, "+", 2.int32)
        "1 +2" becomes CstCall(1.int32, "+", 2.int32)
        "1 -2" becomes CstCall(1.int32, "-", 2.int32)
        "1 +2.0" becomes CstCall(1.int32, "+", 2.float64)
        "1 -2.0" becomes CstCall(1.int32, "-", 2.float64)
        "1 +2_i64" becomes CstCall(1.int32, "+", 2.int64)
        "1 -2_i64" becomes CstCall(1.int32, "-", 2.int64)
        "1\n+2" becomes listOf(1.int32, 2.int32).expressions
        "1;+2" becomes listOf(1.int32, 2.int32).expressions
        "1 - 2" becomes CstCall(1.int32, "-", 2.int32)
        "1 -\n2" becomes CstCall(1.int32, "-", 2.int32)
        "1\n-2" becomes listOf(1.int32, (-2).int32).expressions
        "1;-2" becomes listOf(1.int32, (-2).int32).expressions
        "1 * 2" becomes CstCall(1.int32, "*", 2.int32)
        "1 * -2" becomes CstCall(1.int32, "*", (-2).int32)
        "2 * 3 + 4 * 5" becomes CstCall(CstCall(2.int32, "*", 3.int32), "+", CstCall(4.int32, "*", 5.int32))
        "1 / 2" becomes CstCall(1.int32, "/", 2.int32)
        "1 / -2" becomes CstCall(1.int32, "/", (-2).int32)
        "2 / 3 + 4 / 5" becomes CstCall(CstCall(2.int32, "/", 3.int32), "+", CstCall(4.int32, "/", 5.int32))
        "2 * (3 + 4)" becomes CstCall(2.int32, "*", CstExpressions(listOf(CstCall(3.int32, "+", 4.int32))))
        "a = 1; b = 2; c = 3; a-b-c" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstAssign("b".variable, 2.int32),
            CstAssign("c".variable, 3.int32),
            CstCall(CstCall("a".variable, "-", "b".variable), "-", "c".variable),
        ).expressions
        "a = 1; b = 2; c = 3; a-b -c" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstAssign("b".variable, 2.int32),
            CstAssign("c".variable, 3.int32),
            CstCall(CstCall("a".variable, "-", "b".variable), "-", "c".variable),
        ).expressions
        "1/2" becomes CstCall(1.int32, "/", 2.int32)
        "1 + /foo/" becomes CstCall(1.int32, "+", "foo".regex)
        "1+0" becomes CstCall(1.int32, "+", 0.int32)
        "a = 1; a /b" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstCall("a".variable, "/", "b".call)
        ).expressions
        "a = 1; a/b" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstCall("a".variable, "/", "b".call)
        ).expressions
        "a = 1; (a)/b" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstCall(CstExpressions(listOf("a".variable)), "/", "b".call)
        ).expressions
        "_ = 1" becomes CstAssign(underscore, 1.int32)
        "@foo/2" becomes CstCall("@foo".instanceVar, "/", 2.int32)
        "@@foo/2" becomes CstCall("@@foo".classVar, "/", 2.int32)
        "1+2*3" becomes CstCall(1.int32, "+", CstCall(2.int32, "*", 3.int32))
        "foo[] /2" becomes CstCall(CstCall("foo".call, "[]"), "/", 2.int32)
        "foo[1] /2" becomes CstCall(CstCall("foo".call, "[]", 1.int32), "/", 2.int32)
        "[1] /2" becomes CstCall(listOf(1.int32).array, "/", 2.int32)
        "2**3**4" becomes CstCall(2.int32, "**", CstCall(3.int32, "**", 4.int32))

        "!1" becomes CstNot(1.int32)
        "- 1" becomes CstCall(1.int32, "-")
        "+ 1" becomes CstCall(1.int32, "+")
        "~ 1" becomes CstCall(1.int32, "~")
        "1.~" becomes CstCall(1.int32, "~")
        "1.!" becomes CstNot(1.int32)
        "1 && 2" becomes CstAnd(1.int32, 2.int32)
        "1 || 2" becomes CstOr(1.int32, 2.int32)
        "&- 1" becomes CstCall(1.int32, "&-")
        "&+ 1" becomes CstCall(1.int32, "&+")

        "1 <=> 2" becomes CstCall(1.int32, "<=>", 2.int32)
        "1 !~ 2" becomes CstCall(1.int32, "!~", 2.int32)

        "a = 1" becomes CstAssign("a".variable, 1.int32)
        "a = b = 2" becomes CstAssign("a".variable, CstAssign("b".variable, 2.int32))

        "a, b = 1, 2" becomes CstMultiAssign(listOf("a".variable, "b".variable), listOf(1.int32, 2.int32))
        "a, b = 1" becomes CstMultiAssign(listOf("a".variable, "b".variable), listOf(1.int32))
        "_, _ = 1, 2" becomes CstMultiAssign(listOf(underscore, underscore), listOf(1.int32, 2.int32))
        "a[0], a[1] = 1, 2" becomes CstMultiAssign(
            listOf(CstCall("a".call, "[]", 0.int32), CstCall("a".call, "[]", 1.int32)),
            listOf(1.int32, 2.int32)
        )
        "a[], a[] = 1, 2" becomes CstMultiAssign(
            listOf(CstCall("a".call, "[]"), CstCall("a".call, "[]")),
            listOf(1.int32, 2.int32)
        )
        "a.foo, a.bar = 1, 2" becomes CstMultiAssign(
            listOf(CstCall("a".call, "foo"), CstCall("a".call, "bar")),
            listOf(1.int32, 2.int32)
        )
        "x = 0; a, b = x += 1" becomes CstExpressions(
            listOf(
                CstAssign("x".variable, 0.int32),
                CstMultiAssign(
                    listOf("a".variable, "b".variable),
                    listOf(CstOpAssign("x".variable, "+", 1.int32))
                )
            )
        )
        "a, b = 1, 2 if 3" becomes CstIf(
            3.int32,
            CstMultiAssign(
                listOf("a".variable, "b".variable),
                listOf(1.int32, 2.int32)
            )
        )

        "*a = 1" becomes CstMultiAssign(
            listOf("a".variable.splat),
            listOf(1.int32)
        )
        "*a = 1, 2" becomes CstMultiAssign(
            listOf("a".variable.splat),
            listOf(1.int32, 2.int32)
        )
        "*_ = 1, 2" becomes CstMultiAssign(
            listOf(underscore.splat), listOf(1.int32, 2.int32)
        )

        "*a, b = 1" becomes CstMultiAssign(
            listOf("a".variable.splat, "b".variable),
            listOf(1.int32)
        )
        "a, *b = 1" becomes CstMultiAssign(
            listOf("a".variable, "b".variable.splat),
            listOf(1.int32)
        )
        "a, *b = 1, 2" becomes CstMultiAssign(
            listOf("a".variable, "b".variable.splat),
            listOf(1.int32, 2.int32)
        )
        "*a, b = 1, 2, 3, 4" becomes CstMultiAssign(
            listOf("a".variable.splat, "b".variable),
            listOf(1.int32, 2.int32, 3.int32, 4.int32)
        )
        "a, b, *c = 1" becomes CstMultiAssign(
            listOf("a".variable, "b".variable, "c".variable.splat),
            listOf(1.int32)
        )
        "a, b, *c = 1, 2" becomes CstMultiAssign(
            listOf("a".variable, "b".variable, "c".variable.splat),
            listOf(1.int32, 2.int32)
        )
        "_, *_, _, _ = 1, 2, 3" becomes CstMultiAssign(
            listOf(underscore, underscore.splat, underscore, underscore),
            listOf(1.int32, 2.int32, 3.int32)
        )

        "*a.foo, a.bar = 1" becomes CstMultiAssign(
            listOf(CstCall("a".call, "foo").splat, CstCall("a".call, "bar")),
            listOf(1.int32)
        )
        "a.foo, *a.bar = 1" becomes CstMultiAssign(
            listOf(CstCall("a".call, "foo"), CstCall("a".call, "bar").splat),
            listOf(1.int32)
        )

        "@a, b = 1, 2" becomes CstMultiAssign(
            listOf("@a".instanceVar, "b".variable),
            listOf(1.int32, 2.int32)
        )
        "@@a, b = 1, 2" becomes CstMultiAssign(
            listOf("@@a".classVar, "b".variable),
            listOf(1.int32, 2.int32)
        )

        "あ.い, う.え.お = 1, 2" becomes CstMultiAssign(
            listOf(CstCall("あ".call, "い"), CstCall(CstCall("う".call, "え"), "お")),
            listOf(1.int32, 2.int32)
        )

        "def foo\n1\nend" becomes CstDef("foo", body = 1.int32)
        "def downto(n)\n1\nend" becomes CstDef("downto", listOf("n".arg), 1.int32)
        "def foo ; 1 ; end" becomes CstDef("foo", body = 1.int32)
        "def foo; end" becomes CstDef("foo")
        "def foo(var); end" becomes CstDef("foo", listOf("var".arg))
        "def foo(\nvar); end" becomes CstDef("foo", listOf("var".arg))
        "def foo(\nvar\n); end" becomes CstDef("foo", listOf("var".arg))
        "def foo(var1, var2); end" becomes CstDef("foo", listOf("var1".arg, "var2".arg))
        "def foo; 1; 2; end" becomes CstDef("foo", body = listOf(1.int32, 2.int32).expressions)
        "def foo=(value); end" becomes CstDef("foo=", listOf("value".arg))
        "def foo(n); foo(n -1); end" becomes CstDef(
            "foo",
            listOf("n".arg),
            "foo".call(CstCall("n".variable, "-", 1.int32)))
        "def type(type); end" becomes CstDef("type", listOf("type".arg))

        listOf(
            "begin", "nil", "true", "false", "yield", "with", "abstract",
            "def", "macro", "require", "case", "select", "if", "unless", "include",
            "extend", "class", "struct", "module", "enum", "while", "until", "return",
            "next", "break", "lib", "fun", "alias", "pointerof", "sizeof",
            "instance_sizeof", "offsetof", "typeof", "private", "protected", "asm", "out",
            "end", "self", "in"
        ).forEach { kw ->
            "def foo($kw foo); end" becomes CstDef(
                "foo",
                listOf(CstArg("foo", externalName = kw))
            )
            "def foo(@$kw); end" becomes CstDef(
                "foo",
                listOf(CstArg("__arg0", externalName = kw)),
                listOf(CstAssign("@$kw".instanceVar, "__arg0".variable)).expressions
            )
            "def foo(@@$kw); end" becomes CstDef(
                "foo",
                listOf(CstArg("__arg0", externalName = kw)),
                listOf(CstAssign("@@$kw".classVar, "__arg0".variable)).expressions
            )
            "def foo(x @$kw); end" becomes CstDef(
                "foo",
                listOf(CstArg("__arg0", externalName = "x")),
                listOf(CstAssign("@$kw".instanceVar, "__arg0".variable)).expressions
            )
            "def foo(x @@$kw); end" becomes CstDef(
                "foo",
                listOf(CstArg("__arg0", externalName = "x")),
                listOf(CstAssign("@@$kw".classVar, "__arg0".variable)).expressions
            )
        }

        "def self.foo\n1\nend" becomes CstDef("foo", body = 1.int32, receiver = "self".variable)
        "def self.foo()\n1\nend" becomes CstDef("foo", body = 1.int32, receiver = "self".variable)
        "def self.foo=\n1\nend" becomes CstDef("foo=", body = 1.int32, receiver = "self".variable)
        "def self.foo=()\n1\nend" becomes CstDef("foo=", body = 1.int32, receiver = "self".variable)
        "def Foo.foo\n1\nend" becomes CstDef("foo", body = 1.int32, receiver = "Foo".path)
        "def Foo::Bar.foo\n1\nend" becomes CstDef("foo", body = 1.int32, receiver = listOf("Foo", "Bar").path)

        "def foo; a; end" becomes CstDef("foo", body = "a".call)
        "def foo(a); a; end" becomes CstDef("foo", listOf("a".arg), "a".variable)
        "def foo; a = 1; a; end" becomes CstDef(
            "foo",
            body = listOf(CstAssign("a".variable, 1.int32), "a".variable).expressions
        )
        "def foo; a = 1; a {}; end" becomes CstDef(
            "foo",
            body = listOf(
                CstAssign("a".variable, 1.int32),
                CstCall(null, "a", block = CstBlock.EMPTY)
            ).expressions
        )
        "def foo; a = 1; x { a }; end" becomes CstDef(
            "foo",
            body = listOf(
                CstAssign("a".variable, 1.int32),
                CstCall(null, "x", block = CstBlock(body = "a".variable))
            ).expressions
        )
        "def foo; x { |a| a }; end" becomes CstDef(
            "foo",
            body = CstCall(null, "x", block = CstBlock(listOf("a".variable), "a".variable))
        )
        "def foo; x { |_| 1 }; end" becomes CstDef(
            "foo",
            body = CstCall(null, "x", block = CstBlock(listOf("_".variable), 1.int32))
        )
        "def foo; x { |a, *b| b }; end" becomes CstDef(
            "foo",
            body = CstCall(
                null,
                "x",
                block = CstBlock(listOf("a".variable, "b".variable), "b".variable, splatIndex = 1)
            )
        )

        "def foo(var = 1); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", 1.int32))
        )
        "def foo(var : Int); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = "Int".path))
        )
        "def foo(var : self); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = self))
        )
        "def foo(var : self?); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = CstUnion(listOf(self, "Nil".globalPath))))
        )
        "def foo(var : self.class); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = CstMetaclass(self)))
        )
        "def foo(var : self*); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = self.pointerOf))
        )
        "def foo(var : Int | Double); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = CstUnion(listOf("Int".path, "Double".path))))
        )
        "def foo(var : Int?); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = CstUnion(listOf("Int".path, "Nil".globalPath))))
        )
        "def foo(var : Int*); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = "Int".path.pointerOf))
        )
        "def foo(var : Int**); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = "Int".path.pointerOf.pointerOf))
        )
        "def foo(var : Int -> Double); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = CstProcNotation(listOf("Int".path), "Double".path)))
        )
        "def foo(var : Int, Float -> Double); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = CstProcNotation(listOf("Int".path, "Float".path), "Double".path)))
        )
        "def foo(var : (Int, Float -> Double)); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = CstProcNotation(listOf("Int".path, "Float".path), "Double".path)))
        )
        "def foo(var : (Int, Float) -> Double); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = CstProcNotation(listOf("Int".path, "Float".path), "Double".path)))
        )
        "def foo(var : Char[256]); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = "Char".staticArrayOf(256)))
        )
        "def foo(var : Char[N]); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", restriction = "Char".staticArrayOf("N".path)))
        )
        "def foo(var : Int32 = 1); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", 1.int32, "Int32".path))
        )
        "def foo(var : Int32 -> = 1); end" becomes CstDef(
            "foo",
            listOf(CstArg("var", 1.int32, CstProcNotation(listOf("Int32".path))))
        )

        "def foo; yield; end" becomes CstDef(
            "foo",
            body = CstYield.EMPTY,
            blockArity = 0
        )
        "def foo; yield 1; end" becomes CstDef(
            "foo",
            body = CstYield(listOf(1.int32)),
            blockArity = 1
        )
        "def foo; yield 1; yield; end" becomes CstDef(
            "foo",
            body = listOf(CstYield(listOf(1.int32)), CstYield.EMPTY).expressions,
            blockArity = 1
        )
        "def foo; yield(1); end" becomes CstDef(
            "foo",
            body = (CstYield(listOf(1.int32), hasParentheses = true)),
            blockArity = 1
        )
        "def foo(a, b = a); end" becomes CstDef(
            "foo",
            listOf(CstArg("a"), CstArg("b", "a".variable))
        )
        "def foo(&block); end" becomes CstDef(
            "foo",
            blockArg = CstArg("block"),
            blockArity = 0
        )
        "def foo(&); end" becomes CstDef(
            "foo",
            blockArg = CstArg(""),
            blockArity = 0
        )
        "def foo(&\n); end" becomes CstDef(
            "foo",
            blockArg = CstArg(""),
            blockArity = 0
        )
        "def foo(a, &block); end" becomes CstDef(
            "foo",
            listOf(CstArg("a")),
            blockArg = CstArg("block"),
            blockArity = 0
        )
        "def foo(a, &block : Int -> Double); end" becomes CstDef(
            "foo",
            listOf(CstArg("a")),
            blockArg = CstArg("block", restriction = CstProcNotation(listOf("Int".path), "Double".path)),
            blockArity = 1
        )
        "def foo(a, & : Int -> Double); end" becomes CstDef(
            "foo",
            listOf(CstArg("a")),
            blockArg = CstArg("", restriction = CstProcNotation(listOf("Int".path), "Double".path)),
            blockArity = 1
        )
        "def foo(a, &block : Int, Float -> Double); end" becomes CstDef(
            "foo",
            listOf(CstArg("a")),
            blockArg = CstArg("block", restriction = CstProcNotation(listOf("Int".path, "Float".path), "Double".path)),
            blockArity = 2
        )
        "def foo(a, &block : Int, self -> Double); end" becomes CstDef(
            "foo",
            listOf(CstArg("a")),
            blockArg = CstArg("block", restriction = CstProcNotation(listOf("Int".path, self), "Double".path)),
            blockArity = 2
        )
        "def foo(a, &block : -> Double); end" becomes CstDef(
            "foo",
            listOf(CstArg("a")),
            blockArg = CstArg("block", restriction = CstProcNotation(emptyList(), "Double".path)),
            blockArity = 0
        )
        "def foo(a, &block : Int -> ); end" becomes CstDef(
            "foo",
            listOf(CstArg("a")),
            blockArg = CstArg("block", restriction = CstProcNotation(listOf("Int".path))),
            blockArity = 1
        )
        "def foo(a, &block : self -> self); end" becomes CstDef(
            "foo",
            listOf(CstArg("a")),
            blockArg = CstArg("block", restriction = CstProcNotation(listOf(self), self)),
            blockArity = 1
        )
        "def foo(a, &block : Foo); end" becomes CstDef(
            "foo",
            listOf(CstArg("a")),
            blockArg = CstArg("block", restriction = "Foo".path),
            blockArity = 0
        )
        "def foo; with a yield; end" becomes CstDef(
            "foo",
            body = CstYield(scope = "a".call),
            blockArity = 1
        )
        "def foo; with a yield 1; end" becomes CstDef(
            "foo",
            body = CstYield(listOf(1.int32), "a".call),
            blockArity = 1
        )
        "def foo; a = 1; with a yield a; end" becomes CstDef(
            "foo",
            body = listOf(CstAssign("a".variable, 1.int32), CstYield(listOf("a".variable), "a".variable)).expressions,
            blockArity = 1
        )
        "def foo(@var); end" becomes CstDef(
            "foo",
            listOf(CstArg("var")),
            listOf(CstAssign("@var".instanceVar, "var".variable)).expressions
        )
        "def foo(@var); 1; end" becomes CstDef(
            "foo",
            listOf(CstArg("var")),
            listOf(CstAssign("@var".instanceVar, "var".variable), 1.int32).expressions
        )
        "def foo(@var = 1); 1; end" becomes CstDef(
            "foo",
            listOf(CstArg("var", 1.int32)),
            listOf(CstAssign("@var".instanceVar, "var".variable), 1.int32).expressions
        )
        "def foo(@@var); end" becomes CstDef(
            "foo",
            listOf(CstArg("var")),
            listOf(CstAssign("@@var".classVar, "var".variable)).expressions
        )
        "def foo(@@var); 1; end" becomes CstDef(
            "foo",
            listOf(CstArg("var")),
            listOf(CstAssign("@@var".classVar, "var".variable), 1.int32).expressions
        )
        "def foo(@@var = 1); 1; end" becomes CstDef(
            "foo",
            listOf(CstArg("var", 1.int32)),
            listOf(CstAssign("@@var".classVar, "var".variable), 1.int32).expressions
        )
        "def foo(&@block); end" becomes CstDef(
            "foo",
            body = CstAssign("@block".instanceVar, "block".variable),
            blockArg = CstArg("block"),
            blockArity = 0
        )

        "def foo(@[Foo] var); end" becomes CstDef(
            "foo",
            listOf("var".arg(annotations = listOf("Foo".ann)))
        )
        "def foo(@[Foo] outer inner); end" becomes CstDef(
            "foo",
            listOf("inner".arg(annotations = listOf("Foo".ann), externalName = "outer"))
        )
        "def foo(@[Foo]  var); end" becomes CstDef(
            "foo",
            listOf("var".arg(annotations = listOf("Foo".ann)))
        )
        "def foo(a, @[Foo] var); end" becomes CstDef(
            "foo",
            listOf("a".arg, "var".arg(annotations = listOf("Foo".ann)))
        )
        "def foo(a, @[Foo] &block); end" becomes CstDef(
            "foo",
            listOf("a".arg),
            blockArg = "block".arg(annotations = listOf("Foo".ann)),
            blockArity = 0
        )
        "def foo(@[Foo] @var); end" becomes CstDef(
            "foo",
            listOf("var".arg(annotations = listOf("Foo".ann))),
            listOf(CstAssign("@var".instanceVar, "var".variable)).expressions
        )
        "def foo(@[Foo] var : Int32); end" becomes CstDef(
            "foo",
            listOf("var".arg(restriction = "Int32".path, annotations = listOf("Foo".ann)))
        )
        "def foo(@[Foo] @[Bar] var : Int32); end" becomes CstDef(
            "foo",
            listOf("var".arg(restriction = "Int32".path, annotations = listOf("Foo".ann, "Bar".ann)))
        )
        "def foo(@[Foo] &@block); end" becomes CstDef(
            "foo",
            body = CstAssign("@block".instanceVar, "block".variable),
            blockArg = "block".arg(annotations = listOf("Foo".ann)),
            blockArity = 0
        )
        "def foo(@[Foo] *args); end" becomes CstDef(
            "foo",
            args = listOf("args".arg(annotations = listOf("Foo".ann))),
            splatIndex = 0
        )
        "def foo(@[Foo] **args); end" becomes CstDef(
            "foo",
            doubleSplat = "args".arg(annotations = listOf("Foo".ann)))
        """
          def foo(
            @[Foo]
            id : Int32,
            @[Bar] name : String
          ); end
        """.trimIndent() becomes CstDef(
            "foo",
            listOf(
                "id".arg(restriction = "Int32".path, annotations = listOf("Foo".ann)),
                "name".arg(restriction = "String".path, annotations = listOf("Bar".ann))
            )
        )

        "def foo(\n&block\n); end" becomes CstDef(
            "foo",
            blockArg = CstArg("block"),
            blockArity = 0
        )
        "def foo(&block :\n Int ->); end" becomes CstDef(
            "foo",
            blockArg = CstArg("block", restriction = CstProcNotation(listOf("Int".path))),
            blockArity = 1
        )
        "def foo(&block : Int ->\n); end" becomes CstDef(
            "foo",
            blockArg = CstArg("block", restriction = CstProcNotation(listOf("Int".path))),
            blockArity = 1
        )

        "def foo(a, &block : *Int -> ); end" becomes CstDef(
            "foo",
            listOf("a".arg),
            blockArg = CstArg("block", restriction = CstProcNotation(listOf("Int".path.splat))),
            blockArity = 1
        )

        "def foo(x, *args, y = 2); 1; end" becomes CstDef(
            "foo",
            args = listOf("x".arg, "args".arg, CstArg("y", defaultValue = 2.int32)),
            body = 1.int32,
            splatIndex = 1
        )
        "def foo(x, *args, y = 2, w, z = 3); 1; end" becomes CstDef(
            "foo",
            args = listOf(
                "x".arg,
                "args".arg,
                "y".arg(defaultValue = 2.int32),
                "w".arg,
                "z".arg(defaultValue = 3.int32)
            ),
            body = 1.int32,
            splatIndex = 1
        )
        "def foo(x, *, y); 1; end" becomes CstDef(
            "foo",
            args = listOf("x".arg, "".arg, "y".arg),
            body = 1.int32,
            splatIndex = 1
        )
        "def foo(x, *, y, &); 1; end" becomes CstDef(
            "foo",
            args = listOf("x".arg, "".arg, "y".arg),
            body = 1.int32,
            splatIndex = 1,
            blockArg = "".arg,
            blockArity = 0
        )

        "def foo(**args)\n1\nend" becomes CstDef(
            "foo",
            body = 1.int32,
            doubleSplat = "args".arg
        )
        "def foo(x, **args)\n1\nend" becomes CstDef(
            "foo",
            body = 1.int32,
            args = listOf("x".arg),
            doubleSplat = "args".arg
        )
        "def foo(x, **args, &block)\n1\nend" becomes CstDef(
            "foo",
            body = 1.int32,
            args = listOf("x".arg),
            doubleSplat = "args".arg,
            blockArg = "block".arg,
            blockArity = 0
        )
        "def foo(**args)\nargs\nend" becomes CstDef(
            "foo",
            body = "args".variable,
            doubleSplat = "args".arg
        )
        "def foo(x = 1, **args)\n1\nend" becomes CstDef(
            "foo",
            body = 1.int32,
            args = listOf("x".arg(defaultValue = 1.int32)),
            doubleSplat = "args".arg
        )
        "def foo(**args : Foo)\n1\nend" becomes CstDef(
            "foo",
            body = 1.int32,
            doubleSplat = "args".arg(restriction = "Foo".path)
        )
        "def foo(**args : **Foo)\n1\nend" becomes CstDef(
            "foo",
            body = 1.int32,
            doubleSplat = "args".arg(restriction = CstDoubleSplat("Foo".path))
        )

        "def foo(x y); y; end" becomes CstDef(
            "foo",
            args = listOf("y".arg(externalName = "x")),
            body = "y".variable
        )
        "def foo(x @var); end" becomes CstDef(
            "foo",
            listOf("var".arg(externalName = "x")),
            listOf(CstAssign("@var".instanceVar, "var".variable)).expressions
        )
        "def foo(x @@var); end" becomes CstDef(
            "foo",
            listOf("var".arg(externalName = "x")),
            listOf(CstAssign("@@var".classVar, "var".variable)).expressions
        )

        "def foo(\"bar qux\" y); y; end)" becomes CstDef(
            "foo",
            args = listOf("y".arg(externalName = "bar qux")),
            body = "y".variable
        )

        "macro foo(**args)\n1\nend" becomes CstMacro(
            "foo",
            body = "1\n".macroLiteral,
            doubleSplat = "args".arg
        )

        "abstract def foo" becomes CstDef("foo", isAbstract = true)
        "abstract def foo; 1" becomes listOf(CstDef("foo", isAbstract = true), 1.int32).expressions
        "abstract def foo\n1" becomes listOf(CstDef("foo", isAbstract = true), 1.int32).expressions
        "abstract def foo(x)" becomes CstDef("foo", listOf("x".arg), isAbstract = true)

        "def foo(x : U) forall U; end" becomes CstDef(
            "foo",
            args = listOf("x".arg(restriction = "U".path)),
            freeVars = listOf("U")
        )
        "def foo(x : U) forall T, U; end" becomes CstDef(
            "foo",
            args = listOf("x".arg(restriction = "U".path)),
            freeVars = listOf("T", "U")
        )
        "def foo(x : U) : Int32 forall T, U; end" becomes CstDef(
            "foo",
            args = listOf("x".arg(restriction = "U".path)),
            returnType = "Int32".path,
            freeVars = listOf("T", "U")
        )

        "foo" becomes "foo".call
        "foo()" becomes "foo".call
        "foo(1)" becomes "foo".call(1.int32)
        "foo 1" becomes "foo".call(1.int32)
        "foo 1\n" becomes "foo".call(1.int32)
        "foo 1;" becomes "foo".call(1.int32)
        "foo 1, 2" becomes "foo".call(1.int32, 2.int32)
        "foo (1 + 2), 3" becomes "foo".call(CstExpressions(listOf(CstCall(1.int32, "+", 2.int32))), 3.int32)
        "foo(1 + 2)" becomes "foo".call(CstCall(1.int32, "+", 2.int32))
        "foo -1.0, -2.0" becomes "foo".call((-1).float64, (-2).float64)
        "foo(\n1)" becomes "foo".call(1.int32)
        "::foo" becomes "foo".globalCall

        "foo + 1" becomes CstCall("foo".call, "+", 1.int32)
        "foo +1" becomes CstCall(null, "foo", 1.int32)
        "foo +1.0" becomes CstCall(null, "foo", 1.float64)
        "foo +1_i64" becomes CstCall(null, "foo", 1.int64)
        "foo = 1; foo +1" becomes listOf(
            CstAssign("foo".variable, 1.int32),
            CstCall("foo".variable, "+", 1.int32)
        ).expressions
        "foo = 1; foo(+1)" becomes listOf(
            CstAssign("foo".variable, 1.int32),
            CstCall(null, "foo", 1.int32)
        ).expressions
        "foo = 1; foo -1" becomes listOf(
            CstAssign("foo".variable, 1.int32),
            CstCall("foo".variable, "-", 1.int32)
        ).expressions
        "foo = 1; foo(-1)" becomes listOf(
            CstAssign("foo".variable, 1.int32),
            CstCall(null, "foo", (-1).int32)
        ).expressions
        "foo = 1; b = 2; foo -b" becomes listOf(
            CstAssign("foo".variable, 1.int32),
            CstAssign("b".variable, 2.int32),
            CstCall("foo".variable, "-", "b".variable)
        ).expressions
        "foo = 1; b = 2; foo +b" becomes listOf(
            CstAssign("foo".variable, 1.int32),
            CstAssign("b".variable, 2.int32),
            CstCall("foo".variable, "+", "b".variable)
        ).expressions
        "foo = 1; foo a: 1" becomes listOf(
            CstAssign("foo".variable, 1.int32),
            CstCall(null, "foo", namedArgs = listOf("a".namedArg(1.int32)))
        ).expressions
        "foo = 1; foo {}" becomes listOf(
            CstAssign("foo".variable, 1.int32),
            CstCall(null, "foo", block = CstBlock.EMPTY)
        ).expressions
        "foo = 1; foo &x" becomes listOf(
            CstAssign("foo".variable, 1.int32),
            CstCall(null, "foo", blockArg = "x".call)
        ).expressions
        "def foo(x)\n x\nend; foo = 1; b = 2; foo -b" becomes listOf(
            CstDef("foo", listOf("x".arg), "x".variable),
            CstAssign("foo".variable, 1.int32),
            CstAssign("b".variable, 2.int32),
            CstCall("foo".variable, "-", "b".variable)
        ).expressions
        "def foo(x)\n x\nend; foo = 1; b = 2; foo +b" becomes listOf(
            CstDef("foo", listOf("x".arg), "x".variable),
            CstAssign("foo".variable, 1.int32),
            CstAssign("b".variable, 2.int32),
            CstCall("foo".variable, "+", "b".variable)
        ).expressions

        "foo(&block)" becomes CstCall(null, "foo", blockArg = "block".call)
        "foo &block" becomes CstCall(null, "foo", blockArg = "block".call)
        "a.foo &block" becomes CstCall("a".call, "foo", blockArg = "block".call)
        "a.foo(&block)" becomes CstCall("a".call, "foo", blockArg = "block".call)

        "foo.[0]" becomes CstCall("foo".call, "[]", 0.int32)
        "foo.[0] = 1" becomes CstCall("foo".call, "[]=", listOf(0.int32, 1.int32))

        "foo(a: 1, b: 2)" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("a".namedArg(1.int32), "b".namedArg(2.int32))
        )
        "foo(1, a: 1, b: 2)" becomes CstCall(
            null,
            "foo",
            listOf(1.int32),
            namedArgs = listOf("a".namedArg(1.int32), "b".namedArg(2.int32))
        )
        "foo a: 1, b: 2" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("a".namedArg(1.int32), "b".namedArg(2.int32))
        )
        "foo 1, a: 1, b: 2" becomes CstCall(
            null,
            "foo",
            listOf(1.int32),
            namedArgs = listOf("a".namedArg(1.int32), "b".namedArg(2.int32))
        )
        "foo 1, a: 1, b: 2\n1" becomes listOf(
            CstCall(
                null,
                "foo",
                listOf(1.int32),
                namedArgs = listOf("a".namedArg(1.int32), "b".namedArg(2.int32))
            ),
            1.int32
        ).expressions
        "foo(a: 1\n)" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("a".namedArg(1.int32))
        )
        "foo(\na: 1,\n)" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("a".namedArg(1.int32))
        )

        "foo(\"foo bar\": 1, \"baz\": 2)" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("foo bar".namedArg(1.int32), "baz".namedArg(2.int32))
        )
        "foo \"foo bar\": 1, \"baz\": 2)" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("foo bar".namedArg(1.int32), "baz".namedArg(2.int32))
        )

        "foo(Foo: 1, Bar: 2)" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("Foo".namedArg(1.int32), "Bar".namedArg(2.int32))
        )

        "x.foo(a: 1, b: 2)" becomes CstCall(
            "x".call,
            "foo",
            namedArgs = listOf("a".namedArg(1.int32), "b".namedArg(2.int32))
        )
        "x.foo a: 1, b: 2 " becomes CstCall(
            "x".call,
            "foo",
            namedArgs = listOf("a".namedArg(1.int32), "b".namedArg(2.int32))
        )

        "x[a: 1, b: 2]" becomes CstCall(
            "x".call,
            "[]",
            namedArgs = listOf("a".namedArg(1.int32), "b".namedArg(2.int32))
        )
        "x[a: 1, b: 2,]" becomes CstCall(
            "x".call,
            "[]",
            namedArgs = listOf("a".namedArg(1.int32), "b".namedArg(2.int32))
        )
        "x[{1}]" becomes CstCall(
            "x".call,
            "[]",
            CstTupleLiteral(listOf(1.int32))
        )
        "x[+ 1]" becomes CstCall(
            "x".call,
            "[]",
            CstCall(1.int32, "+")
        )

        "foo(a: 1, &block)" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("a".namedArg(1.int32)),
            blockArg = "block".call
        )
        "foo a: 1, &block" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("a".namedArg(1.int32)),
            blockArg = "block".call
        )
        "foo a: b(1) do\nend" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("a".namedArg(CstCall(null, "b", 1.int32))),
            block = CstBlock.EMPTY
        )

        "Foo.bar x.y do\nend" becomes CstCall(
            "Foo".path,
            "bar",
            args = listOf(CstCall("x".call, "y")),
            block = CstBlock.EMPTY
        )

        "x = 1; foo x do\nend" becomes listOf(
            CstAssign("x".variable, 1.int32),
            CstCall(null, "foo", listOf("x".variable), CstBlock.EMPTY)
        ).expressions
        "x = 1; foo x { }" becomes listOf(
            CstAssign("x".variable, 1.int32),
            CstCall(null, "foo", listOf(CstCall(null, "x", block = CstBlock.EMPTY)))
        ).expressions
        "x = 1; foo x {\n}" becomes listOf(
            CstAssign("x".variable, 1.int32),
            CstCall(null, "foo", listOf(CstCall(null, "x", block = CstBlock.EMPTY)))
        ).expressions
        "foo x do\nend" becomes CstCall(
            null,
            "foo",
            listOf("x".call),
            CstBlock.EMPTY
        )
        "foo x, y do\nend" becomes CstCall(
            null,
            "foo",
            listOf("x".call, "y".call),
            CstBlock.EMPTY
        )
        "foo(bar do\nend)" becomes CstCall(
            null,
            "foo",
            listOf(CstCall(null, "bar", emptyList(), CstBlock.EMPTY))
        )
        "foo(bar { })" becomes CstCall(
            null,
            "foo",
            listOf(CstCall(null, "bar", emptyList(), CstBlock.EMPTY))
        )
        "(bar do\nend)" becomes CstExpressions(
            listOf(CstCall(null, "bar", emptyList(), CstBlock.EMPTY))
        )
        "(bar do\nend)" becomes CstExpressions(
            listOf(CstCall(null, "bar", emptyList(), CstBlock.EMPTY))
        )
        "(foo bar do\nend)" becomes CstExpressions(
            listOf(CstCall(null, "foo", listOf("bar".call), CstBlock.EMPTY))
        )
        "(baz; bar do\nend)" becomes CstExpressions(
            listOf("baz".call, CstCall(null, "bar", emptyList(), CstBlock.EMPTY))
        )
        "(bar {})" becomes CstExpressions(
            listOf(CstCall(null, "bar", emptyList(), CstBlock.EMPTY))
        )
        "(a;\nb)" becomes CstExpressions(
            listOf(CstCall(null, "a"), CstCall(null, "b"))
        )
        "1.x; foo do\nend" becomes listOf(
            CstCall(1.int32, "x"),
            CstCall(null, "foo", block = CstBlock.EMPTY)
        ).expressions
        "x = 1; foo.bar x do\nend" becomes listOf(
            CstAssign("x".variable, 1.int32),
            CstCall("foo".call, "bar", listOf("x".variable), CstBlock.EMPTY)
        ).expressions

        "foo do\n//\nend" becomes CstCall(null, "foo", emptyList(), CstBlock(body = "".regex))
        "foo x do\n//\nend" becomes CstCall(null, "foo", listOf("x".call), CstBlock(body = "".regex))
        "foo(x) do\n//\nend" becomes CstCall(null, "foo", listOf("x".call), CstBlock(body = "".regex))

        "foo !false" becomes CstCall(null, "foo", listOf(CstNot(false.bool)))
        "!a && b" becomes CstAnd(CstNot("a".call), "b".call)

        "foo.bar.baz" becomes CstCall(CstCall("foo".call, "bar"), "baz")
        "f.x Foo.new" becomes CstCall("f".call, "x", listOf(CstCall("Foo".path, "new")))
        "f.x = Foo.new" becomes CstCall("f".call, "x=", listOf(CstCall("Foo".path, "new")))
        "f.x = - 1" becomes CstCall("f".call, "x=", listOf(CstCall(1.int32, "-")))

        listOf("+", "-", "*", "/", "//", "%", "|", "&", "^", "**", "<<", ">>", "&+", "&-", "&*").forEach { op ->
            "f.x $op= 2" becomes CstOpAssign(CstCall("f".call, "x"), op, 2.int32)
        }

        listOf("/", "<", "<=", "==", "!=", "=~", "!~", ">", ">=", "+", "-", "*", "/", "~", "%", "&", "|", "^", "**", "===").forEach { op ->
            "def $op; end;" becomes CstDef(op)
            "def $op(); end;" becomes CstDef(op)
            "def self.$op; end;" becomes CstDef(op, receiver = "self".variable)
            "def self.$op(); end;" becomes CstDef(op, receiver = "self".variable)
        }

        listOf("<<", "<", "<=", "==", ">>", ">", ">=", "+", "-", "*", "/", "//", "%", "|", "&", "^", "**", "===", "=~", "!~", "&+", "&-", "&*", "&**").forEach { op ->
            "1 $op 2" becomes CstCall(1.int32, op, 2.int32)
            "n $op 2" becomes CstCall("n".call, op, 2.int32)
            "foo(n $op 2)" becomes CstCall(null, "foo", CstCall("n".call, op, 2.int32))
            "foo(0, n $op 2)" becomes CstCall(null, "foo", listOf(0.int32, CstCall("n".call, op, 2.int32)))
            "foo(a: n $op 2)" becomes CstCall(
                null,
                "foo",
                emptyList(),
                namedArgs = listOf("a".namedArg(CstCall("n".call, op, 2.int32)))
            )
            "foo(z: 0, a: n $op 2)" becomes CstCall(
                null,
                "foo",
                emptyList(),
                namedArgs = listOf("z".namedArg(0.int32), "a".namedArg(CstCall("n".call, op, 2.int32)))
            )
            "def $op(); end" becomes CstDef(op)

            "foo = 1; ->foo.$op(Int32)" becomes listOf(
                CstAssign("foo".variable, 1.int32),
                CstProcPointer("foo".variable, op, listOf("Int32".path))
            ).expressions
            "->Foo.$op(Int32)" becomes CstProcPointer("Foo".path, op, listOf("Int32".path))
        }

        listOf("[]", "[]=").forEach { op ->
            "foo = 1; ->foo.$op(Int32)" becomes listOf(
                CstAssign("foo".variable, 1.int32),
                CstProcPointer("foo".variable, op, listOf("Int32".path))
            ).expressions
            "->Foo.$op(Int32)" becomes CstProcPointer(
                "Foo".path,
                op,
                listOf("Int32".path)
            )
        }

        listOf("bar", "+", "-", "*", "/", "<", "<=", "==", ">", ">=", "%", "|", "&", "^", "**", "===", "=~", "!~").forEach { name ->
            "foo.$name" becomes CstCall("foo".call, name)
            "foo.$name 1, 2" becomes CstCall("foo".call, name, listOf(1.int32, 2.int32))
            "foo.$name(1, 2)" becomes CstCall("foo".call, name, listOf(1.int32, 2.int32))
        }

        listOf("+", "-", "*", "/", "//", "%", "|", "&", "^", "**", "<<", ">>", "&+", "&-", "&*").forEach { op ->
            "a = 1; a $op= 1" becomes listOf(
                CstAssign("a".variable, 1.int32),
                CstOpAssign("a".variable, op, 1.int32)
            ).expressions
            "a = 1; a $op=\n1" becomes listOf(
                CstAssign("a".variable, 1.int32),
                CstOpAssign("a".variable, op, 1.int32)
            ).expressions
            "a.b $op=\n1" becomes CstOpAssign(CstCall("a".call, "b"), op, 1.int32)
        }

        "a = 1; a &&= 1" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstOpAssign("a".variable, "&&", 1.int32)
        ).expressions
        "a = 1; a ||= 1" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstOpAssign("a".variable, "||", 1.int32)
        ).expressions

        "a = 1; a[2] &&= 3" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstOpAssign(CstCall("a".variable, "[]", 2.int32), "&&", 3.int32)
        ).expressions
        "a = 1; a[2] ||= 3" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstOpAssign(CstCall("a".variable, "[]", 2.int32), "||", 3.int32)
        ).expressions

        "if foo; 1; end" becomes CstIf("foo".call, 1.int32)
        "if foo\n1\nend" becomes CstIf("foo".call, 1.int32)
        "if foo; 1; else; 2; end" becomes CstIf("foo".call, 1.int32, 2.int32)
        "if foo\n1\nelse\n2\nend" becomes CstIf("foo".call, 1.int32, 2.int32)
        "if foo; 1; elsif bar; 2; else 3; end" becomes CstIf("foo".call, 1.int32, CstIf("bar".call, 2.int32, 3.int32))

        "include Foo" becomes CstInclude("Foo".path)
        "include Foo\nif true; end" becomes listOf(
            CstInclude("Foo".path),
            CstIf(true.bool)
        ).expressions
        "extend Foo" becomes CstExtend("Foo".path)
        "extend Foo\nif true; end" becomes listOf(
            CstExtend("Foo".path),
            CstIf(true.bool)
        ).expressions
        "extend self" becomes CstExtend(self)

        "unless foo; 1; end" becomes CstUnless("foo".call, 1.int32)
        "unless foo; 1; else; 2; end" becomes CstUnless("foo".call, 1.int32, 2.int32)

        "class Foo; end" becomes CstClassDef("Foo".path)
        "class Foo\nend" becomes CstClassDef("Foo".path)
        "class Foo\ndef foo; end; end" becomes CstClassDef("Foo".path, listOf(CstDef("foo")).expressions)
        "class Foo < Bar; end" becomes CstClassDef("Foo".path, superclass = "Bar".path)
        "class Foo(T); end" becomes CstClassDef("Foo".path, typeVars = listOf("T"))
        "class Foo(T1); end" becomes CstClassDef("Foo".path, typeVars = listOf("T1"))
        "class Foo(Type); end" becomes CstClassDef("Foo".path, typeVars = listOf("Type"))
        "abstract class Foo; end" becomes CstClassDef("Foo".path, isAbstract = true)
        "abstract struct Foo; end" becomes CstClassDef("Foo".path, isAbstract = true, isStruct = true)

        "class Foo < self; end" becomes CstClassDef("Foo".path, superclass = self)

        "module Foo(*T); end" becomes CstModuleDef("Foo".path, typeVars = listOf("T"), splatIndex = 0)
        "class Foo(*T); end" becomes CstClassDef("Foo".path, typeVars = listOf("T"), splatIndex = 0)
        "class Foo(T, *U); end" becomes CstClassDef("Foo".path, typeVars = listOf("T", "U"), splatIndex = 1)

        "x : Foo(A, *B, C)" becomes CstTypeDeclaration(
            "x".variable,
            CstGeneric("Foo".path, listOf("A".path, "B".path.splat, "C".path))
        )
        "x : *T -> R" becomes CstTypeDeclaration(
            "x".variable,
            CstProcNotation(listOf("T".path.splat), "R".path)
        )
        "def foo(x : *T -> R); end" becomes CstDef(
            "foo",
            args = listOf(CstArg("x", restriction = CstProcNotation(listOf("T".path.splat), "R".path)))
        )

        "foo result : Int32; result" becomes listOf(
            CstCall(null, "foo", CstTypeDeclaration("result".variable, "Int32".path)),
            CstCall(null, "result")
        ).expressions

        "foo(x: result : Int32); result" becomes listOf(
            CstCall(
                null,
                "foo",
                namedArgs= listOf("x".namedArg(CstTypeDeclaration("result".variable, "Int32".path)))
            ),
            CstCall(null, "result"),
        ).expressions

        """
        foo(
          begin
            result : Int32 = 1
            result
          end
        )
        """ becomes CstCall(
            null,
            "foo",
            listOf(
                CstTypeDeclaration("result".variable, "Int32".path, 1.int32),
                "result".variable,
            ).expressions
        )

        """
        foo(x:
          begin
            result : Int32 = 1
            result
          end
        )    
        """ becomes CstCall(
            null,
            "foo",
            namedArgs = listOf(
                "x".namedArg(
                    listOf(
                        CstTypeDeclaration("result".variable, "Int32".path, 1.int32),
                        "result".variable,
                    ).expressions
                )
            )
        )

        "struct Foo; end" becomes CstClassDef("Foo".path, isStruct = true)

        "Foo()" becomes CstGeneric("Foo".path, emptyList())
        "Foo(T)" becomes CstGeneric("Foo".path, listOf("T".path))
        "Foo(T | U)" becomes CstGeneric(
            "Foo".path,
            listOf(CstUnion(listOf("T".path, "U".path)))
        )
        "Foo(Bar(T | U))" becomes CstGeneric(
            "Foo".path,
            listOf(CstGeneric("Bar".path, listOf(CstUnion(listOf("T".path, "U".path)))))
        )
        "Foo(Bar())" becomes CstGeneric(
            "Foo".path,
            listOf(CstGeneric("Bar".path, emptyList()))
        )
        "Foo(T?)" becomes CstGeneric(
            "Foo".path,
            listOf(CstUnion(listOf("T".path, CstPath.global("Nil"))))
        )
        "Foo(1)" becomes CstGeneric("Foo".path, listOf(1.int32))
        "Foo(T, 1)" becomes CstGeneric("Foo".path, listOf("T".path, 1.int32))
        "Foo(T, U, 1)" becomes CstGeneric("Foo".path, listOf("T".path, "U".path, 1.int32))
        "Foo(T, 1, U)" becomes CstGeneric("Foo".path, listOf("T".path, 1.int32, "U".path))
        "Foo(typeof(1))" becomes CstGeneric("Foo".path, listOf(1.int32.typeOf))
        "Foo(typeof(1), typeof(2))" becomes CstGeneric(
            "Foo".path,
            listOf(1.int32.typeOf, 2.int32.typeOf)
        )
        "Foo({X, Y})" becomes CstGeneric(
            "Foo".path,
            listOf(CstGeneric("Tuple".globalPath, listOf("X".path, "Y".path)))
        )
        "Foo({X, Y,})" becomes CstGeneric(
            "Foo".path,
            listOf(CstGeneric("Tuple".globalPath, listOf("X".path, "Y".path)))
        )
        "Foo({*X, *{Y}})" becomes CstGeneric(
            "Foo".path,
            listOf(
                CstGeneric(
                    "Tuple".globalPath,
                    listOf("X".path.splat, CstGeneric("Tuple".globalPath, listOf("Y".path)).splat)
                )
            )
        )
        "Foo({->})" becomes CstGeneric(
            "Foo".path,
            listOf(CstGeneric("Tuple".globalPath, listOf(CstProcNotation.EMPTY)))
        )
        "Foo({String, ->})" becomes CstGeneric(
            "Foo".path,
            listOf(CstGeneric("Tuple".globalPath, listOf("String".path, CstProcNotation.EMPTY)))
        )
        "Foo({String, ->, ->})" becomes CstGeneric(
            "Foo".path,
            listOf(
                CstGeneric("Tuple".globalPath, listOf("String".path, CstProcNotation.EMPTY, CstProcNotation.EMPTY))
            )
        )
        "[] of {String, ->}" becomes CstArrayLiteral(
            emptyList(),
            CstGeneric("Tuple".globalPath, listOf("String".path, CstProcNotation.EMPTY))
        )
        "x([] of Foo, Bar.new)" becomes CstCall(
            null,
            "x",
            listOf(
                CstArrayLiteral(emptyList(), "Foo".path),
                CstCall("Bar".path, "new")
            )
        )

        "foo[bar { 1 }]" becomes CstCall(
            "foo".call,
            "[]",
            CstCall(null, "bar", block = CstBlock(body = 1.int32))
        )
        "foo.[bar { 1 }]" becomes CstCall(
            "foo".call,
            "[]",
            CstCall(null, "bar", block = CstBlock(body = 1.int32))
        )
        "foo.[](bar { 1 })" becomes CstCall(
            "foo".call,
            "[]",
            CstCall(null, "bar", block = CstBlock(body = 1.int32))
        )
        "foo[bar do; 1; end]" becomes CstCall(
            "foo".call,
            "[]",
            CstCall(null, "bar", block = CstBlock(body = 1.int32))
        )
        "foo.[bar do; 1; end]" becomes CstCall(
            "foo".call,
            "[]",
            CstCall(null, "bar", block = CstBlock(body = 1.int32))
        )
        "foo.[](bar do; 1; end)" becomes CstCall(
            "foo".call,
            "[]",
            CstCall(null, "bar", block = CstBlock(body = 1.int32))
        )

        "Foo(x: U)" becomes CstGeneric("Foo".path, emptyList(), namedArgs = listOf("x".namedArg("U".path)))
        "Foo(x: U, y: V)" becomes CstGeneric("Foo".path, emptyList(), namedArgs = listOf("x".namedArg("U".path), "y".namedArg("V".path)))
        "Foo(X: U, Y: V)" becomes CstGeneric("Foo".path, emptyList(), namedArgs = listOf("X".namedArg("U".path), "Y".namedArg("V".path)))

        "Foo(\"foo bar\": U)" becomes CstGeneric(
            "Foo".path,
            emptyList(),
            namedArgs = listOf("foo bar".namedArg("U".path))
        )
        "Foo(\"foo\": U, \"bar\": V)" becomes CstGeneric(
            "Foo".path,
            emptyList(),
            namedArgs = listOf("foo".namedArg("U".path), "bar".namedArg("V".path))
        )

        "Foo({x: X})" becomes CstGeneric(
            "Foo".path,
            listOf(
                CstGeneric(CstPath.global("NamedTuple"), emptyList(), namedArgs = listOf("x".namedArg("X".path)))
            )
        )
        "Foo({x: X, y: Y})" becomes CstGeneric(
            "Foo".path,
            listOf(
                CstGeneric(
                    CstPath.global("NamedTuple"),
                    emptyList(),
                    namedArgs = listOf("x".namedArg("X".path), "y".namedArg("Y".path))
                )
            )
        )
        "Foo({X: X, Y: Y})" becomes CstGeneric(
            "Foo".path,
            listOf(
                CstGeneric(
                    CstPath.global("NamedTuple"),
                    emptyList(),
                    namedArgs = listOf("X".namedArg("X".path), "Y".namedArg("Y".path))
                )
            )
        )
        "Foo(T, {x: X})" becomes CstGeneric(
            "Foo".path,
            listOf(
                "T".path,
                CstGeneric(
                    CstPath.global("NamedTuple"),
                    emptyList(),
                    namedArgs = listOf("x".namedArg("X".path))
                )
            )
        )
        "Foo({x: X, typeof: Y})" becomes CstGeneric(
            "Foo".path,
            listOf(
                CstGeneric(
                    CstPath.global("NamedTuple"),
                    emptyList(),
                    namedArgs = listOf("x".namedArg("X".path), "typeof".namedArg("Y".path))
                )
            )
        )

        "Foo({\"foo bar\": X})" becomes CstGeneric(
            "Foo".path,
            listOf(
                CstGeneric(
                    CstPath.global("NamedTuple"),
                    emptyList(),
                    namedArgs = listOf("foo bar".namedArg("X".path))
                )
            )
        )
        "Foo({\"foo\": X, \"bar\": Y})" becomes CstGeneric(
            "Foo".path,
            listOf(
                CstGeneric(
                    CstPath.global("NamedTuple"),
                    emptyList(),
                    namedArgs = listOf("foo".namedArg("X".path), "bar".namedArg("Y".path))
                )
            )
        )

        "Foo{\"x\" => \"y\"}" becomes CstHashLiteral(
            listOf(CstHashLiteral.Entry("x".string, "y".string)),
            receiverType = "Foo".path
        )
        "::Foo{\"x\" => \"y\"}" becomes CstHashLiteral(
            listOf(CstHashLiteral.Entry("x".string, "y".string)),
            receiverType = CstPath.global("Foo")
        )

        "Foo(*T)" becomes CstGeneric("Foo".path, listOf("T".path.splat))

        "Foo(X, sizeof(Int32))" becomes CstGeneric(
            "Foo".path,
            listOf("X".path, CstSizeOf("Int32".path))
        )
        "Foo(X, instance_sizeof(Int32))" becomes CstGeneric(
            "Foo".path,
            listOf("X".path, CstInstanceSizeOf("Int32".path))
        )
        "Foo(X, offsetof(Foo, @a))" becomes CstGeneric(
            "Foo".path,
            listOf("X".path, CstOffsetOf("Foo".path, "@a".instanceVar))
        )

        "Foo(\n)" becomes CstGeneric("Foo".path, emptyList())
        "Foo(\nT\n)" becomes CstGeneric("Foo".path, listOf("T".path))
        "Foo(\nT,\nU,\n)" becomes CstGeneric("Foo".path, listOf("T".path, "U".path))
        "Foo(\nx:\nT,\ny:\nU,\n)" becomes CstGeneric(
            "Foo".path,
            emptyList(),
            namedArgs = listOf("x".namedArg("T".path), "y".namedArg("U".path))
        )

        "module Foo; end" becomes CstModuleDef("Foo".path)
        "module Foo\ndef foo; end; end" becomes CstModuleDef("Foo".path, CstDef("foo"))
        "module Foo(T); end" becomes CstModuleDef("Foo".path, typeVars = listOf("T"))

        "while true; end;" becomes CstWhile(true.bool)
        "while true; 1; end;" becomes CstWhile(true.bool, 1.int32)

        "until true; end;" becomes CstUntil(true.bool)
        "until true; 1; end;" becomes CstUntil(true.bool, 1.int32)

        "foo do; 1; end" becomes CstCall(null, "foo", block = CstBlock(body = 1.int32))
        "foo do |a|; 1; end" becomes CstCall(null, "foo", block = CstBlock(listOf("a".variable), 1.int32))

        "foo { 1 }" becomes CstCall(null, "foo", block = CstBlock(body = 1.int32))
        "foo { |a| 1 }" becomes CstCall(null, "foo", block = CstBlock(listOf("a".variable), 1.int32))
        "foo { |a, b| 1 }" becomes CstCall(null, "foo", block = CstBlock(listOf("a".variable, "b".variable), 1.int32))
        "foo { |a, b, | 1 }" becomes CstCall(null, "foo", block = CstBlock(listOf("a".variable, "b".variable), 1.int32))
        "1.foo do; 1; end" becomes CstCall(1.int32, "foo", block = CstBlock(body = 1.int32))
        "a b() {}" becomes CstCall(null, "a", CstCall(null, "b", block = CstBlock.EMPTY))

        "1 ? 2 : 3" becomes CstIf(1.int32, 2.int32, 3.int32)
        "1 ? a : b" becomes CstIf(1.int32, "a".call, "b".call)
        "1 ? a : b ? c : 3" becomes CstIf(1.int32, "a".call, CstIf("b".call, "c".call, 3.int32))
        "a ? 1 : b ? 2 : c ? 3 : 0" becomes CstIf(
            "a".call,
            1.int32,
            CstIf("b".call, 2.int32, CstIf("c".call, 3.int32, 0.int32))
        )
        """a ? 1
        : b""" becomes CstIf("a".call, 1.int32, "b".call)
        """a ? 1 :
        b ? 2 :
        c ? 3
        : 0""" becomes CstIf("a".call, 1.int32, CstIf("b".call, 2.int32, CstIf("c".call, 3.int32, 0.int32)))
        """a ? 1
        : b ? 2
        : c ? 3
        : 0""" becomes CstIf("a".call, 1.int32, CstIf("b".call, 2.int32, CstIf("c".call, 3.int32, 0.int32)))
        """a ?
        b ? b1 : b2
        : c ? 3
        : 0""" becomes CstIf("a".call, CstIf("b".call, "b1".call, "b2".call), CstIf("c".call, 3.int32, 0.int32))

        "1 if 3" becomes CstIf(3.int32, 1.int32)
        "1 unless 3" becomes CstUnless(3.int32, 1.int32)
        "r = 1; r.x += 2" becomes listOf(
            CstAssign("r".variable, 1.int32),
            CstOpAssign(CstCall("r".variable, "x"), "+", 2.int32)
        ).expressions

        "foo if 3" becomes CstIf(3.int32, "foo".call)
        "foo unless 3" becomes CstUnless(3.int32, "foo".call)

        "a = 1; a += 10 if a += 20" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstIf(CstOpAssign("a".variable, "+", 20.int32), CstOpAssign("a".variable, "+", 10.int32))
        ).expressions
        "puts a if true" becomes CstIf(true.bool, CstCall(null, "puts", "a".call))
        "puts ::foo" becomes CstCall(null, "puts", CstCall(null, "foo", isGlobal = true))

        "x = 2; foo do bar x end" becomes listOf(
            CstAssign("x".variable, 2.int32),
            CstCall(null, "foo", block = CstBlock(body = CstCall(null, "bar", "x".variable)))
        ).expressions

        "puts __FILE__" becomes CstCall(null, "puts", "/a.cr".string)
        "puts __DIR__" becomes CstCall(null, "puts", "".string)
        "puts __LINE__" becomes CstCall(null, "puts", 1.int32)
        "puts _" becomes CstCall(null, "puts", underscore)

        "\n\n__LINE__" becomes 3.int32
        "__FILE__" becomes "/a.cr".string
        "__DIR__" becomes "".string

        listOf("break" to ::CstBreak, "return" to ::CstReturn, "next" to ::CstNext).forEach { (keyword, klass) ->
            keyword becomes klass(null, null)
            "$keyword;" becomes klass(null, null)
            "$keyword 1" becomes klass(1.int32, null)
            "$keyword 1, 2" becomes klass(CstTupleLiteral(listOf(1.int32, 2.int32)), null)
            "$keyword {1, 2}" becomes klass(CstTupleLiteral(listOf(1.int32, 2.int32)), null)
            "$keyword {1 => 2}" becomes klass(CstHashLiteral(listOf(CstHashLiteral.Entry(1.int32, 2.int32))), null)
            "$keyword 1 if true" becomes CstIf(true.bool, klass(1.int32, null))
            "$keyword if true" becomes CstIf(true.bool, klass(null, null))

            "$keyword *1" becomes klass(CstTupleLiteral(listOf(1.int32.splat)), null)
            "$keyword *1, 2" becomes klass(CstTupleLiteral(listOf(1.int32.splat, 2.int32)), null)
            "$keyword 1, *2" becomes klass(CstTupleLiteral(listOf(1.int32, 2.int32.splat)), null)
            "$keyword *{1, 2}" becomes klass(CstTupleLiteral(listOf(CstTupleLiteral(listOf(1.int32, 2.int32)).splat)), null)
        }

        "yield" becomes CstYield.EMPTY
        "yield;" becomes CstYield.EMPTY
        "yield 1" becomes CstYield(listOf(1.int32))
        "yield 1 if true" becomes CstIf(true.bool, CstYield(listOf(1.int32)))
        "yield if true" becomes CstIf(true.bool, CstYield.EMPTY)

        "Int" becomes "Int".path

        "Int[]" becomes CstCall("Int".path, "[]")
        "def []; end" becomes CstDef("[]")
        "def []?; end" becomes CstDef("[]?")
        "def []=(value); end" becomes CstDef("[]=", listOf("value".arg))
        "def self.[]; end" becomes CstDef("[]", receiver = "self".variable)
        "def self.[]?; end" becomes CstDef("[]?", receiver = "self".variable)

        "Int[8]" becomes CstCall("Int".path, "[]", 8.int32)
        "Int[8, 4]" becomes CstCall("Int".path, "[]", listOf(8.int32, 4.int32))
        "Int[8, 4,]" becomes CstCall("Int".path, "[]", listOf(8.int32, 4.int32))
        "Int[8]?" becomes CstCall("Int".path, "[]?", 8.int32)
        "x[0] ? 1 : 0" becomes CstIf(CstCall("x".call, "[]", 0.int32), 1.int32, 0.int32)

        "def [](x); end" becomes CstDef("[]", listOf("x".arg))

        "foo[0] = 1" becomes CstCall("foo".call, "[]=", listOf(0.int32, 1.int32))
        "foo[0] = 1 if 2" becomes CstIf(2.int32, CstCall("foo".call, "[]=", listOf(0.int32, 1.int32)))

        "begin; 1; end;" becomes CstExpressions(listOf(1.int32))
        "begin; 1; 2; 3; end;" becomes listOf(1.int32, 2.int32, 3.int32).expressions

        "self" becomes "self".variable

        "@foo" becomes "@foo".instanceVar
        "@foo = 1" becomes CstAssign("@foo".instanceVar, 1.int32)
        "-@foo" becomes CstCall("@foo".instanceVar, "-")

        "var.@foo" becomes CstReadInstanceVar("var".call, "@foo")
        "var.@foo.@bar" becomes CstReadInstanceVar(CstReadInstanceVar("var".call, "@foo"), "@bar")

        "@@foo" becomes "@@foo".classVar
        "@@foo = 1" becomes CstAssign("@@foo".classVar, 1.int32)
        "-@@foo" becomes CstCall("@@foo".classVar, "-")

        "call @foo.bar" becomes CstCall(null, "call", CstCall("@foo".instanceVar, "bar"))
        "call \"foo\"" becomes CstCall(null, "call", "foo".string)

        "def foo; end; if false; 1; else; 2; end" becomes listOf(
            CstDef("foo"),
            CstIf(false.bool, 1.int32, 2.int32)
        ).expressions

        "A.new(\"x\", B.new(\"y\"))" becomes CstCall(
            "A".path,
            "new",
            listOf(
                "x".string,
                CstCall("B".path, "new", "y".string)
            )
        )

        "foo [1]" becomes CstCall(null, "foo", listOf(1.int32).array)
        "foo.bar [1]" becomes CstCall("foo".call, "bar", listOf(1.int32).array)

        "class Foo; end\nwhile true; end" becomes listOf(CstClassDef("Foo".path), CstWhile(true.bool)).expressions
        "while true; end\nif true; end" becomes listOf(CstWhile(true.bool), CstIf(true.bool)).expressions
        "(1)\nif true; end" becomes listOf(CstExpressions(listOf(1.int32)), CstIf(true.bool)).expressions
        "begin\n1\nend\nif true; end" becomes listOf(CstExpressions(listOf(1.int32)), CstIf(true.bool)).expressions

        "Foo::Bar" becomes listOf("Foo", "Bar").path

        "lib LibC\nend" becomes CstLibDef("LibC".path)
        "lib LibC\nfun getchar\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getchar")
        )
        "lib LibC\nfun getchar(...)\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getchar", isVariadic = true)
        )
        "lib LibC\nfun getchar : Int\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getchar", returnType = "Int".path)
        )
        "lib LibC\nfun getchar : (->)?\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getchar", returnType = CstUnion(listOf(CstProcNotation.EMPTY, "Nil".globalPath)))
        )
        "lib LibC\nfun getchar(Int, Float)\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getchar", listOf("".arg(restriction = "Int".path), "".arg(restriction = "Float".path)))
        )
        "lib LibC\nfun getchar(a : Int, b : Float)\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getchar", listOf("a".arg(restriction = "Int".path), "b".arg(restriction = "Float".path)))
        )
        "lib LibC\nfun getchar(a : Int)\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getchar", listOf("a".arg(restriction = "Int".path)))
        )
        "lib LibC\nfun getchar(a : Int, b : Float) : Int\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef(
                "getchar",
                listOf("a".arg(restriction = "Int".path), "b".arg(restriction = "Float".path)),
                "Int".path
            )
        )
        "lib LibC; fun getchar(a : Int, b : Float) : Int; end" becomes CstLibDef(
            "LibC".path,
            CstFunDef(
                "getchar",
                listOf("a".arg(restriction = "Int".path), "b".arg(restriction = "Float".path)),
                "Int".path
            )
        )
        "lib LibC; fun foo(a : Int*); end" becomes CstLibDef(
            "LibC".path,
            CstFunDef("foo", listOf("a".arg(restriction = "Int".path.pointerOf)))
        )
        "lib LibC; fun foo(a : Int**); end" becomes CstLibDef(
            "LibC".path,
            CstFunDef("foo", listOf("a".arg(restriction = "Int".path.pointerOf.pointerOf)))
        )
        "lib LibC; fun foo : Int*; end" becomes CstLibDef(
            "LibC".path,
            CstFunDef("foo", returnType = "Int".path.pointerOf)
        )
        "lib LibC; fun foo : Int**; end" becomes CstLibDef(
            "LibC".path,
            CstFunDef("foo", returnType = "Int".path.pointerOf.pointerOf)
        )
        "lib LibC; fun foo(a : ::B, ::C -> ::D); end" becomes CstLibDef(
            "LibC".path,
            CstFunDef(
                "foo",
                listOf(
                    "a".arg(restriction = CstProcNotation(listOf("B".globalPath, "C".globalPath), "D".globalPath))
                )
            )
        )
        "lib LibC; type A = B; end" becomes CstLibDef(
            "LibC".path,
            CstTypeDef("A", "B".path)
        )
        "lib LibC; type A = B*; end" becomes CstLibDef(
            "LibC".path,
            CstTypeDef("A", "B".path.pointerOf)
        )
        "lib LibC; type A = B**; end" becomes CstLibDef(
            "LibC".path,
            CstTypeDef("A", "B".path.pointerOf.pointerOf)
        )
        "lib LibC; type A = B.class; end" becomes CstLibDef(
            "LibC".path,
            CstTypeDef("A", CstMetaclass("B".path))
        )
        "lib LibC; struct Foo; end end" becomes CstLibDef(
            "LibC".path,
            CstCStructOrUnionDef("Foo")
        )
        "lib LibC; struct Foo; x : Int; y : Float; end end" becomes CstLibDef(
            "LibC".path,
            CstCStructOrUnionDef(
                "Foo",
                listOf(
                    CstTypeDeclaration("x".variable, "Int".path),
                    CstTypeDeclaration("y".variable, "Float".path)
                ).expressions
            )
        )
        "lib LibC; struct Foo; x : Int*; end end" becomes CstLibDef(
            "LibC".path,
            CstCStructOrUnionDef("Foo", CstTypeDeclaration("x".variable, "Int".path.pointerOf))
        )
        "lib LibC; struct Foo; x : Int**; end end" becomes CstLibDef(
            "LibC".path,
            CstCStructOrUnionDef(
                "Foo",
                CstTypeDeclaration("x".variable, "Int".path.pointerOf.pointerOf)
            )
        )
        "lib LibC; struct Foo; x, y, z : Int; end end" becomes CstLibDef(
            "LibC".path,
            CstCStructOrUnionDef(
                "Foo",
                listOf(
                    CstTypeDeclaration("x".variable, "Int".path),
                    CstTypeDeclaration("y".variable, "Int".path),
                    CstTypeDeclaration("z".variable, "Int".path)
                ).expressions
            )
        )
        "lib LibC; union Foo; end end" becomes CstLibDef(
            "LibC".path,
            CstCStructOrUnionDef("Foo", isUnion = true)
        )
        "lib LibC; enum Foo; A\nB; C\nD = 1; end end" becomes CstLibDef(
            "LibC".path,
            CstEnumDef(
                "Foo".path,
                listOf("A".arg, "B".arg, "C".arg, "D".arg(1.int32))
            )
        )
        "lib LibC; enum Foo; A = 1; B; end end" becomes CstLibDef(
            "LibC".path,
            CstEnumDef(
                "Foo".path,
                listOf("A".arg(1.int32), "B".arg)
            )
        )
        "lib LibC; Foo = 1; end" becomes CstLibDef(
            "LibC".path,
            CstAssign("Foo".path, 1.int32)
        )
        "lib LibC\nfun getch = GetChar\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getch", realName = "GetChar")
        )
        "lib LibC\nfun getch = \"get.char\"\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getch", realName = "get.char")
        )
        "lib LibC\nfun getch = \"get.char\" : Int32\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getch", returnType = "Int32".path, realName = "get.char")
        )
        "lib LibC\nfun getch = \"get.char\"(x : Int32)\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("getch", listOf("x".arg(restriction = "Int32".path)), realName = "get.char")
        )
        "lib LibC\n\$errno : Int32\n\$errno2 : Int32\nend" becomes CstLibDef(
            "LibC".path,
            listOf(
                CstExternalVar("errno", "Int32".path),
                CstExternalVar("errno2", "Int32".path)
            ).expressions
        )
        "lib LibC\n\$errno : B, C -> D\nend" becomes CstLibDef(
            "LibC".path,
            CstExternalVar("errno", CstProcNotation(listOf("B".path, "C".path), "D".path))
        )
        "lib LibC\n\$errno = Foo : Int32\nend" becomes CstLibDef(
            "LibC".path,
            CstExternalVar("errno", "Int32".path, "Foo")
        )
        "lib LibC\nalias Foo = Bar\nend" becomes CstLibDef(
            "LibC".path,
            CstAlias("Foo".path, "Bar".path)
        )
        "lib LibC; struct Foo; include Bar; end; end" becomes CstLibDef(
            "LibC".path,
            CstCStructOrUnionDef("Foo", CstInclude("Bar".path))
        )

        "lib LibC\nfun SomeFun\nend" becomes CstLibDef(
            "LibC".path,
            CstFunDef("SomeFun")
        )

        "lib Foo::Bar\nend" becomes CstLibDef(listOf("Foo", "Bar").path)

        "fun foo(x : Int32) : Int64\nx\nend" becomes CstFunDef(
            "foo",
            listOf("x".arg(restriction = "Int32".path)),
            "Int64".path,
            body = "x".variable
        )

        "lib LibC; {{ 1 }}; end" becomes CstLibDef(
            "LibC".path,
            body = CstMacroExpression(1.int32)
        )
        "lib LibC; {% if 1 %}2{% end %}; end" becomes CstLibDef(
            "LibC".path,
            body = CstMacroIf(1.int32, CstMacroLiteral("2"))
        )

        "lib LibC; struct Foo; {{ 1 }}; end; end" becomes CstLibDef(
            "LibC".path,
            body = CstCStructOrUnionDef("Foo", CstMacroExpression(1.int32))
        )
        "lib LibC; struct Foo; {% if 1 %}2{% end %}; end; end" becomes CstLibDef(
            "LibC".path,
            body = CstCStructOrUnionDef("Foo", CstMacroIf(1.int32, CstMacroLiteral("2")))
        )

        "1 .. 2" becomes CstRangeLiteral(1.int32, 2.int32, false)
        "1 ... 2" becomes CstRangeLiteral(1.int32, 2.int32, true)
        "(1 .. )" becomes CstExpressions(listOf(CstRangeLiteral(1.int32, CstNop, false)))
        "(1 ... )" becomes CstExpressions(listOf(CstRangeLiteral(1.int32, CstNop, true)))
        "foo(1.., 2)" becomes CstCall(
            null,
            "foo",
            listOf(CstRangeLiteral(1.int32, CstNop, false), 2.int32)
        )
        "1..;" becomes CstRangeLiteral(1.int32, CstNop, false)
        "1..\n2.." becomes listOf(
            CstRangeLiteral(1.int32, CstNop, false),
            CstRangeLiteral(2.int32, CstNop, false)
        ).expressions
        "{1.. => 2};" becomes CstHashLiteral(
            listOf(CstHashLiteral.Entry(CstRangeLiteral(1.int32, CstNop, false), 2.int32))
        )
        "..2" becomes CstRangeLiteral(CstNop, 2.int32, false)
        "...2" becomes CstRangeLiteral(CstNop, 2.int32, true)
        "foo..2" becomes CstRangeLiteral("foo".call, 2.int32, false)
        "foo ..2" becomes CstRangeLiteral("foo".call, 2.int32, false)
        "foo(..2)" becomes CstCall(null, "foo", CstRangeLiteral(CstNop, 2.int32, false))
        "x[..2]" becomes CstCall("x".call, "[]", CstRangeLiteral(CstNop, 2.int32, false))
        "x[1, ..2]" becomes CstCall(
            "x".call,
            "[]",
            listOf(1.int32, CstRangeLiteral(CstNop, 2.int32, false))
        )
        "{..2}" becomes CstTupleLiteral(listOf(CstRangeLiteral(CstNop, 2.int32, false)))
        "[..2]" becomes CstArrayLiteral(listOf(CstRangeLiteral(CstNop, 2.int32, false)))

        "A = 1" becomes CstAssign("A".path, 1.int32)

        "puts %w(one two)" becomes CstCall(null, "puts", listOf("one".string, "two".string).arrayOf("String".globalPath))
        "puts %w{one two}" becomes CstCall(null, "puts", listOf("one".string, "two".string).arrayOf("String".globalPath))
        "puts %i(one two)" becomes CstCall(null, "puts", listOf("one".symbol, "two".symbol).arrayOf("Symbol".globalPath))
        "puts {{1}}" becomes CstCall(null, "puts", CstMacroExpression(1.int32))
        "puts {{\n1\n}}" becomes CstCall(null, "puts", CstMacroExpression(1.int32))
        "puts {{*1}}" becomes CstCall(null, "puts", CstMacroExpression(1.int32.splat))
        "puts {{**1}}" becomes CstCall(null, "puts", CstMacroExpression(CstDoubleSplat(1.int32)))
        "{{a = 1 if 2}}" becomes CstMacroExpression(CstIf(2.int32, CstAssign("a".variable, 1.int32)))
        "{% a = 1 %}" becomes CstMacroExpression(CstAssign("a".variable, 1.int32), isOutput = false)
        "{%\na = 1\n%}" becomes CstMacroExpression(CstAssign("a".variable, 1.int32), isOutput = false)
        "{% a = 1 if 2 %}" becomes CstMacroExpression(CstIf(2.int32, CstAssign("a".variable, 1.int32)), isOutput = false)
        "{% if 1; 2; end %}" becomes CstMacroExpression(CstIf(1.int32, 2.int32), isOutput = false)
        "{%\nif 1; 2; end\n%}" becomes CstMacroExpression(CstIf(1.int32, 2.int32), isOutput = false)
        "{% unless 1; 2; end %}" becomes CstMacroExpression(CstUnless(1.int32, 2.int32, CstNop), isOutput = false)
        "{% unless 1; 2; else 3; end %}" becomes CstMacroExpression(CstUnless(1.int32, 2.int32, 3.int32), isOutput = false)
        "{%\n1\n2\n3\n%}" becomes CstMacroExpression(listOf(1.int32, 2.int32, 3.int32).expressions, isOutput = false)

        "{{ 1 // 2 }}" becomes CstMacroExpression(CstCall(1.int32, "//", 2.int32))
        "{{ //.options }}" becomes CstMacroExpression(CstCall("".regex, "options"))

        "[] of Int" becomes listOf<CstNode>().arrayOf("Int".path)
        "[1, 2] of Int" becomes listOf(1.int32, 2.int32).arrayOf("Int".path)

        "::A::B" becomes CstPath.global(listOf("A", "B"))

        "macro foo;end" becomes CstMacro("foo", emptyList(), CstExpressions.EMPTY)
        "macro [];end" becomes CstMacro("[]", emptyList(), CstExpressions.EMPTY)
        "macro foo; 1 + 2; end" becomes CstMacro(
            "foo",
            emptyList(),
            " 1 + 2; ".macroLiteral
        )
        "macro foo(x); 1 + 2; end" becomes CstMacro(
            "foo",
            listOf("x".arg),
            " 1 + 2; ".macroLiteral
        )
        "macro foo(x)\n 1 + 2; end" becomes CstMacro(
            "foo",
            listOf("x".arg),
            " 1 + 2; ".macroLiteral
        )
        "macro foo; 1 + 2 {{foo}} 3 + 4; end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                " 1 + 2 ".macroLiteral,
                CstMacroExpression("foo".variable),
                " 3 + 4; ".macroLiteral
            ).expressions
        )
        "macro foo; 1 + 2 {{ foo }} 3 + 4; end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                " 1 + 2 ".macroLiteral,
                CstMacroExpression("foo".variable),
                " 3 + 4; ".macroLiteral
            ).expressions
        )
        "macro foo;bar{% for x in y %}body{% end %}baz;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "bar".macroLiteral,
                CstMacroFor(listOf("x".variable), "y".variable, "body".macroLiteral),
                "baz;".macroLiteral
            ).expressions
        )
        "macro foo;bar{% for x, y in z %}body{% end %}baz;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "bar".macroLiteral,
                CstMacroFor(listOf("x".variable, "y".variable), "z".variable, "body".macroLiteral),
                "baz;".macroLiteral
            ).expressions
        )
        "macro foo;bar{% if x %}body{% end %}baz;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "bar".macroLiteral,
                CstMacroIf("x".variable, "body".macroLiteral),
                "baz;".macroLiteral
            ).expressions
        )
        "macro foo;bar{% if x %}body{% else %}body2{%end%}baz;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "bar".macroLiteral,
                CstMacroIf("x".variable, "body".macroLiteral, "body2".macroLiteral),
                "baz;".macroLiteral
            ).expressions
        )
        "macro foo;bar{% if x %}body{% elsif y %}body2{%end%}baz;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "bar".macroLiteral,
                CstMacroIf("x".variable, "body".macroLiteral, CstMacroIf("y".variable, "body2".macroLiteral)),
                "baz;".macroLiteral
            ).expressions
        )
        "macro foo;bar{% if x %}body{% elsif y %}body2{% else %}body3{%end%}baz;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "bar".macroLiteral,
                CstMacroIf("x".variable, "body".macroLiteral, CstMacroIf("y".variable, "body2".macroLiteral, "body3".macroLiteral)),
                "baz;".macroLiteral
            ).expressions
        )
        "macro foo;bar{% unless x %}body{% end %}baz;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "bar".macroLiteral,
                CstMacroIf("x".variable, CstNop, "body".macroLiteral),
                "baz;".macroLiteral
            ).expressions
        )

        "macro foo;bar{% for x in y %}\\  \n   body{% end %}baz;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "bar".macroLiteral,
                CstMacroFor(listOf("x".variable), "y".variable, "body".macroLiteral),
                "baz;".macroLiteral
            ).expressions
        )
        "macro foo;bar{% for x in y %}\\  \n   body{% end %}\\   baz;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "bar".macroLiteral,
                CstMacroFor(listOf("x".variable), "y".variable, "body".macroLiteral),
                "baz;".macroLiteral
            ).expressions
        )
        "macro foo; 1 + 2 {{foo}}\\ 3 + 4; end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                " 1 + 2 ".macroLiteral,
                CstMacroExpression("foo".variable),
                "3 + 4; ".macroLiteral
            ).expressions
        )

        "macro foo(\na = 0\n)\nend" becomes CstMacro(
            "foo",
            listOf("a".arg(defaultValue = 0.int32)),
            CstExpressions.EMPTY
        )

        "macro foo;{% verbatim do %}1{% foo %}2{% end %};end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                CstMacroVerbatim(
                    listOf(
                        "1".macroLiteral,
                        CstMacroExpression("foo".variable, false),
                        "2".macroLiteral
                    ).expressions
                ),
                ";".macroLiteral
            ).expressions
        )

        "macro foo\n{%\nif 1\n2\nelse\n3\nend\n%}end" becomes CstMacro(
            "foo",
            body = CstMacroExpression(CstIf(1.int32, 2.int32, 3.int32), isOutput = false)
        )

        "macro foo\neenum\nend" becomes CstMacro(
            "foo",
            body = "eenum\n".macroLiteral
        )
        "macro foo\n'\\''\nend" becomes CstMacro(
            "foo",
            body = "'\\''\n".macroLiteral
        )
        "macro foo\n'\\\\'\nend" becomes CstMacro(
            "foo",
            body = "'\\\\'\n".macroLiteral
        )
        "macro foo\n\"\\'\"\nend" becomes CstMacro(
            "foo",
            body = "\"\\'\"\n".macroLiteral
        )
        "macro foo\n\"\\\\\"\nend" becomes CstMacro(
            "foo",
            body = "\"\\\\\"\n".macroLiteral
        )

        "macro foo;bar(end: 1);end" becomes CstMacro(
            "foo",
            body = listOf("bar(".macroLiteral, "end: 1);".macroLiteral).expressions
        )
        "def foo;bar(end: 1);end" becomes CstDef(
            "foo",
            body = CstCall(null, "bar", namedArgs = listOf("end".namedArg(1.int32)))
        )

        "macro foo(@[Foo] var);end" becomes CstMacro(
            "foo",
            listOf("var".arg(annotations = listOf("Foo".ann))),
            CstExpressions.EMPTY
        )
        "macro foo(@[Foo] outer inner);end" becomes CstMacro(
            "foo",
            listOf("inner".arg(annotations = listOf("Foo".ann), externalName = "outer")),
            CstExpressions.EMPTY
        )
        "macro foo(@[Foo]  var);end" becomes CstMacro(
            "foo",
            listOf("var".arg(annotations = listOf("Foo".ann))),
            CstExpressions.EMPTY
        )
        "macro foo(a, @[Foo] var);end" becomes CstMacro(
            "foo",
            listOf("a".arg, "var".arg(annotations = listOf("Foo".ann))),
            CstExpressions.EMPTY
        )
        "macro foo(a, @[Foo] &block);end" becomes CstMacro(
            "foo",
            listOf("a".arg),
            CstExpressions.EMPTY,
            blockArg = "block".arg(annotations = listOf("Foo".ann))
        )
        "macro foo(@[Foo] *args);end" becomes CstMacro(
            "foo",
            listOf("args".arg(annotations = listOf("Foo".ann))),
            CstExpressions.EMPTY,
            splatIndex = 0
        )
        "macro foo(@[Foo] **args);end" becomes CstMacro(
            "foo",
            body = CstExpressions.EMPTY,
            doubleSplat = "args".arg(annotations = listOf("Foo".ann))
        )
        """
        macro foo(
          @[Foo]
          id,
          @[Bar] name
        );end
        """ becomes CstMacro(
            "foo",
            listOf(
                "id".arg(annotations = listOf("Foo".ann)),
                "name".arg(annotations = listOf("Bar".ann))
            ),
            CstExpressions.EMPTY
        )

        "macro foo=;end" becomes CstMacro("foo=", body = CstExpressions.EMPTY)

        listOf("`", "<<", "<", "<=", "==", "===", "!=", "=~", "!~", ">>", ">", ">=",
               "+", "-", "*", "/", "//", "~", "%", "&", "|", "^", "**", "[]?", "[]=", "<=>",
               "&+", "&-", "&*", "&**").forEach { op ->
            "macro $op;end" becomes CstMacro(op, body = CstExpressions.EMPTY)
        }

        "def foo;{{@type}};end" becomes CstDef(
            "foo",
            body = CstMacroExpression("@type".instanceVar),
            isMacroDef = true
        )

        "macro foo;bar{% begin %}body{% end %}baz;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "bar".macroLiteral,
                CstMacroIf(true.bool, "body".macroLiteral),
                "baz;".macroLiteral
            ).expressions
        )

        "macro x\n%{}\nend" becomes CstMacro(
            "x",
            body = CstMacroLiteral("%{}\n")
        )

        "def foo : Int32\n1\nend" becomes CstDef(
            "foo",
            body = 1.int32,
            returnType = "Int32".path
        )
        "def foo(x) : Int32\n1\nend" becomes CstDef(
            "foo",
            args = listOf("x".arg),
            body = 1.int32,
            returnType = "Int32".path
        )

        "abstract def foo : Int32" becomes CstDef(
            "foo",
            returnType = "Int32".path,
            isAbstract = true
        )
        "abstract def foo(x) : Int32" becomes CstDef(
            "foo",
            args = listOf("x".arg),
            returnType = "Int32".path,
            isAbstract = true
        )

        "{% for x in y %}body{% end %}" becomes CstMacroFor(
            listOf("x".variable),
            "y".variable,
            "body".macroLiteral
        )
        "{% for _, x, _ in y %}body{% end %}" becomes CstMacroFor(
            listOf("_".variable, "x".variable, "_".variable),
            "y".variable,
            "body".macroLiteral
        )
        "{% if x %}body{% end %}" becomes CstMacroIf(
            "x".variable,
            "body".macroLiteral
        )
        "{% begin %}{% if true %}if true{% end %}\n{% if true %}end{% end %}{% end %}" becomes CstMacroIf(
            true.bool,
            listOf(
                CstMacroIf(true.bool, "if true".macroLiteral),
                "\n".macroLiteral,
                CstMacroIf(true.bool, "end".macroLiteral)
            ).expressions
        )
        "{{ foo }}" becomes CstMacroExpression("foo".variable)

        "macro foo;%var;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf("var".macroVar, ";".macroLiteral).expressions
        )
        "macro foo;%var{1, x} = hello;end" becomes CstMacro(
            "foo",
            emptyList(),
            listOf(
                "var".macroVar(listOf(1.int32, "x".variable)),
                " = hello;".macroLiteral
            ).expressions
        )

        listOf("if", "unless").forEach { keyword ->
            "macro foo;%var $keyword true;end" becomes CstMacro(
                "foo",
                emptyList(),
                listOf("var".macroVar, " $keyword true;".macroLiteral).expressions
            )
            "macro foo;var $keyword true;end" becomes CstMacro(
                "foo",
                emptyList(),
                "var $keyword true;".macroLiteral
            )
            "macro foo;$keyword %var;true;end;end" becomes CstMacro(
                "foo",
                emptyList(),
                listOf(
                    "$keyword ".macroLiteral,
                    "var".macroVar,
                    ";true;".macroLiteral,
                    "end;".macroLiteral
                ).expressions
            )
            "macro foo;$keyword var;true;end;end" becomes CstMacro(
                "foo",
                emptyList(),
                listOf(
                    "$keyword var;true;".macroLiteral,
                    "end;".macroLiteral
                ).expressions
            )
        }

        "a = 1; pointerof(a)" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstPointerOf("a".variable)
        ).expressions
        "pointerof(@a)" becomes CstPointerOf("@a".instanceVar)
        "a = 1; pointerof(a)" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstPointerOf("a".variable)
        ).expressions
        "pointerof(@a)" becomes CstPointerOf("@a".instanceVar)

        "sizeof(X)" becomes CstSizeOf("X".path)
        "instance_sizeof(X)" becomes CstInstanceSizeOf("X".path)
        "offsetof(X, @a)" becomes CstOffsetOf("X".path, "@a".instanceVar)
        "offsetof(X, 1)" becomes CstOffsetOf("X".path, 1.int32)

        "foo.is_a?(Const)" becomes CstIsA("foo".call, "Const".path)
        "foo.is_a?(Foo | Bar)" becomes CstIsA("foo".call, CstUnion(listOf("Foo".path, "Bar".path)))
        "foo.is_a? Const" becomes CstIsA("foo".call, "Const".path)
        "foo.responds_to?(:foo)" becomes CstRespondsTo("foo".call, "foo")
        "foo.responds_to? :foo" becomes CstRespondsTo("foo".call, "foo")
        "if foo.responds_to? :foo\nx = 1\nend" becomes CstIf(
            CstRespondsTo("foo".call, "foo"),
            CstAssign("x".variable, 1.int32)
        )

        "is_a?(Const)" becomes CstIsA("self".variable, "Const".path)
        "responds_to?(:foo)" becomes CstRespondsTo("self".variable, "foo")
        "nil?" becomes CstIsA("self".variable, "Nil".globalPath, isNilCheck = true)
        "nil?(  )" becomes CstIsA("self".variable, "Nil".globalPath, isNilCheck = true)

        "foo.nil?" becomes CstIsA("foo".call, "Nil".globalPath, isNilCheck = true)
        "foo.nil?(  )" becomes CstIsA("foo".call, "Nil".globalPath, isNilCheck = true)

        "foo &.nil?" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstIsA("__arg0".variable, "Nil".globalPath, isNilCheck = true)
            )
        )
        "foo &.baz.qux do\nend" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall(CstCall("__arg0".variable, "baz"), "qux", block = CstBlock.EMPTY)
            )
        )

        "{{ foo.nil? }}" becomes CstMacroExpression(CstCall("foo".variable, "nil?"))
        "{{ foo &.nil? }}" becomes CstMacroExpression(
            CstCall(
                null,
                "foo",
                block = CstBlock(listOf("__arg0".variable), CstCall("__arg0".variable, "nil?"))
            )
        )
        "{{ foo.nil?(foo) }}" becomes CstMacroExpression(
            CstCall("foo".variable, "nil?", listOf("foo".variable))
        )
        "{{ nil?(foo) }}" becomes CstMacroExpression(
            CstCall(null, "nil?", listOf("foo".variable))
        )

        "foo.!" becomes CstNot("foo".call)
        "foo.!.!" becomes CstNot(CstNot("foo".call))
        "foo.!(  )" becomes CstNot("foo".call)

        "sizeof(\n  Int32\n)" becomes CstSizeOf("Int32".path)
        "instance_sizeof(\n  Int32\n)" becomes CstInstanceSizeOf("Int32".path)
        "typeof(\n  1\n)" becomes CstTypeOf(listOf(1.int32))
        "offsetof(\n  Foo,\n  @foo\n)" becomes CstOffsetOf("Foo".path, "@foo".instanceVar)
        "pointerof(\n  foo\n)" becomes CstPointerOf("foo".call)
        "1.as(\n  Int32\n)" becomes CstCast(1.int32, "Int32".path)
        "1.as?(\n  Int32\n)" becomes CstNilableCast(1.int32, "Int32".path)
        "1.is_a?(\n  Int32\n)" becomes CstIsA(1.int32, "Int32".path)
        "1.responds_to?(\n  :foo\n)" becomes CstRespondsTo(1.int32, "foo")
        "1.nil?(\n)" becomes CstIsA(1.int32, "Nil".globalPath, isNilCheck = true)
        "1.!(\n)" becomes CstNot(1.int32)

        "/foo/" becomes "foo".regex
        "/foo/i" becomes "foo".regex(CstRegexLiteral.IGNORE_CASE)
        "/foo/m" becomes "foo".regex(CstRegexLiteral.MULTILINE)
        "/foo/x" becomes "foo".regex(CstRegexLiteral.EXTENDED)
        "/foo/imximx" becomes "foo".regex(
            CstRegexLiteral.IGNORE_CASE or CstRegexLiteral.MULTILINE or CstRegexLiteral.EXTENDED
        )
        "/fo\\so/" becomes "fo\\so".regex
        "/fo#{1}o/" becomes CstStringInterpolation(
            listOf("fo".string, 1.int32, "o".string)
        ).regex
        "/(fo#{\"bar\"}#{1}o)/" becomes CstStringInterpolation(
            listOf("(fo".string, "bar".string, 1.int32, "o)".string)
        ).regex
        "%r(foo(bar))" becomes "foo(bar)".regex
        "/ /" becomes " ".regex
        "/=/" becomes "=".regex
        "/ hi /" becomes " hi ".regex
        "self / number" becomes CstCall("self".variable, "/", "number".call)
        "a == / /" becomes CstCall("a".call, "==", " ".regex)
        "/ /" becomes " ".regex
        "/ /; / /" becomes listOf(" ".regex, " ".regex).expressions
        "/ /\n/ /" becomes listOf(" ".regex, " ".regex).expressions
        "a = / /" becomes CstAssign("a".variable, " ".regex)
        "(/ /)" becomes CstExpressions(listOf(" ".regex))
        "a = /=/" becomes CstAssign("a".variable, "=".regex)
        "a; if / /; / /; elsif / /; / /; end" becomes listOf(
            "a".call,
            CstIf(" ".regex, " ".regex, CstIf(" ".regex, " ".regex))
        ).expressions
        "a; if / /\n/ /\nelsif / /\n/ /\nend" becomes listOf(
            "a".call,
            CstIf(" ".regex, " ".regex, CstIf(" ".regex, " ".regex))
        ).expressions
        "a; unless / /; / /; else; / /; end" becomes listOf(
            "a".call,
            CstUnless(" ".regex, " ".regex, " ".regex)
        ).expressions
        "a\nunless / /\n/ /\nelse\n/ /\nend" becomes listOf(
            "a".call,
            CstUnless(" ".regex, " ".regex, " ".regex)
        ).expressions
        "a\nwhile / /; / /; end" becomes listOf(
            "a".call,
            CstWhile(" ".regex, " ".regex)
        ).expressions
        "a\nwhile / /\n/ /\nend" becomes listOf(
            "a".call,
            CstWhile(" ".regex, " ".regex)
        ).expressions
        "[/ /, / /]" becomes listOf(" ".regex, " ".regex).array
        "{/ / => / /, / / => / /}" becomes CstHashLiteral(
            listOf(
                CstHashLiteral.Entry(" ".regex, " ".regex),
                CstHashLiteral.Entry(" ".regex, " ".regex)
            )
        )
        "{/ /, / /}" becomes CstTupleLiteral(
            listOf(" ".regex, " ".regex)
        )
        "begin; / /; end" becomes CstExpressions(listOf(" ".regex))
        "begin\n/ /\nend" becomes CstExpressions(listOf(" ".regex))
        "/\\//" becomes "/".regex
        "/\\ /" becomes " ".regex
        "%r(/)" becomes "/".regex
        "%r(\\/)" becomes "/".regex
        "%r(\\ )" becomes " ".regex
        "a()/3" becomes CstCall("a".call, "/", 3.int32)
        "a() /3" becomes CstCall("a".call, "/", 3.int32)
        "a.b() /3" becomes CstCall(CstCall("a".call, "b"), "/", 3.int32)
        "def foo(x = / /); end" becomes CstDef("foo", listOf("x".arg(" ".regex)))
        "begin 1 end / 2" becomes CstCall(CstExpressions(listOf(1.int32)), "/", 2.int32)

        "1 =~ 2" becomes CstCall(1.int32, "=~", 2.int32)
        "1.=~(2)" becomes CstCall(1.int32, "=~", 2.int32)
        "def =~; end" becomes CstDef("=~", emptyList())

        "$~" becomes CstGlobal("$~")
        "$~.foo" becomes CstCall(CstGlobal("$~"), "foo")
        "$0" becomes CstCall(CstGlobal("$~"), "[]", 0.int32)
        "$1" becomes CstCall(CstGlobal("$~"), "[]", 1.int32)
        "$1?" becomes CstCall(CstGlobal("$~"), "[]?", 1.int32)
        "foo $1" becomes CstCall(null, "foo", CstCall(CstGlobal("$~"), "[]", 1.int32))
        "$~ = 1" becomes CstAssign("$~".variable, 1.int32)

        "$?" becomes CstGlobal("$?")
        "$?.foo" becomes CstCall(CstGlobal("$?"), "foo")
        "foo $?" becomes CstCall(null, "foo", CstGlobal("$?"))
        "$? = 1" becomes CstAssign("$?".variable, 1.int32)

        "foo /a/" becomes CstCall(null, "foo", "a".regex)
        "foo(/a/)" becomes CstCall(null, "foo", "a".regex)
        "foo(//)" becomes CstCall(null, "foo", "".regex)
        "foo(regex: //)" becomes CstCall(
            null,
            "foo",
            emptyList(),
            namedArgs = listOf("regex".namedArg("".regex))
        )

        "foo(/ /)" becomes CstCall(null, "foo", " ".regex)
        "foo(/ /, / /)" becomes CstCall(null, "foo", listOf(" ".regex, " ".regex))
        "foo a, / /" becomes CstCall(null, "foo", listOf("a".call, " ".regex))
        "foo /;/" becomes CstCall(null, "foo", ";".regex)

        "foo out x; x" becomes listOf(
            CstCall(null, "foo", CstOut("x".variable)),
            "x".variable
        ).expressions
        "foo(out x); x" becomes listOf(
            CstCall(null, "foo", CstOut("x".variable)), "x".variable
        ).expressions
        "foo out @x; @x" becomes listOf(
            CstCall(null, "foo", CstOut("@x".instanceVar)),
            "@x".instanceVar
        ).expressions
        "foo(out @x); @x" becomes listOf(
            CstCall(null, "foo", CstOut("@x".instanceVar)),
            "@x".instanceVar
        ).expressions
        "foo out _" becomes CstCall(null, "foo", CstOut(underscore))
        "foo z: out x; x" becomes listOf(
            CstCall(
                null,
                "foo",
                namedArgs = listOf("z".namedArg(CstOut("x".variable)))),
            "x".variable
        ).expressions

        "{1 => 2, 3 => 4}" becomes CstHashLiteral(
            listOf(
                CstHashLiteral.Entry(1.int32, 2.int32),
                CstHashLiteral.Entry(3.int32, 4.int32)
            )
        )
        "{1 =>\n2, 3 =>\n4}" becomes CstHashLiteral(
            listOf(
                CstHashLiteral.Entry(1.int32, 2.int32),
                CstHashLiteral.Entry(3.int32, 4.int32)
            )
        )
        "{A::B => 1, C::D => 2}" becomes CstHashLiteral(
            listOf(
                CstHashLiteral.Entry(CstPath(listOf("A", "B")), 1.int32),
                CstHashLiteral.Entry(CstPath(listOf("C", "D")), 2.int32)
            )
        )

        "{a: 1}" becomes CstNamedTupleLiteral(listOf(CstNamedTupleLiteral.Entry("a", 1.int32)))
        "{a: 1, b: 2}" becomes CstNamedTupleLiteral(
            listOf(CstNamedTupleLiteral.Entry("a", 1.int32), CstNamedTupleLiteral.Entry("b", 2.int32))
        )
        "{A: 1, B: 2}" becomes CstNamedTupleLiteral(
            listOf(CstNamedTupleLiteral.Entry("A", 1.int32), CstNamedTupleLiteral.Entry("B", 2.int32))
        )

        "{\"foo\": 1}" becomes CstNamedTupleLiteral(
            listOf(CstNamedTupleLiteral.Entry("foo", 1.int32))
        )
        "{\"foo\": 1, \"bar\": 2}" becomes CstNamedTupleLiteral(
            listOf(
                CstNamedTupleLiteral.Entry("foo", 1.int32),
                CstNamedTupleLiteral.Entry("bar", 2.int32)
            )
        )

        "{} of Int => Double" becomes CstHashLiteral(
            emptyList(),
            CstHashLiteral.Entry("Int".path, "Double".path)
        )
        "{} of Int32 -> Int32 => Int32" becomes CstHashLiteral(
            emptyList(),
            CstHashLiteral.Entry(CstProcNotation(listOf("Int32".path), "Int32".path), "Int32".path)
        )

        "require \"foo\"" becomes CstRequire("foo")
        "require \"foo\"; [1]" becomes listOf(
            CstRequire("foo"),
            listOf(1.int32).array
        ).expressions

        "case 1; when 1; 2; else; 3; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(1.int32), 2.int32)),
            3.int32,
            isExhaustive = false
        )
        "case 1; when 0, 1; 2; else; 3; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(0.int32, 1.int32), 2.int32)),
            3.int32,
            isExhaustive = false
        )
        "case 1\nwhen 1\n2\nelse\n3\nend" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(1.int32), 2.int32)),
            3.int32,
            isExhaustive = false
        )
        "case 1\nwhen 1\n2\nend" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(1.int32), 2.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case / /; when / /; / /; else; / /; end" becomes CstCase(
            " ".regex,
            listOf(CstWhen(listOf(" ".regex), " ".regex)),
            " ".regex,
            isExhaustive = false
        )
        "case / /\nwhen / /\n/ /\nelse\n/ /\nend" becomes CstCase(
            " ".regex,
            listOf(CstWhen(listOf(" ".regex), " ".regex)),
            " ".regex,
            isExhaustive = false
        )

        "case 1; when 1 then 2; else; 3; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(1.int32), 2.int32)),
            3.int32,
            isExhaustive = false
        )
        "case 1; when x then 2; else; 3; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf("x".call), 2.int32)),
            3.int32,
            isExhaustive = false
        )
        "case 1\nwhen 1\n2\nend\nif a\nend" becomes listOf(
            CstCase(
                1.int32,
                listOf(CstWhen(listOf(1.int32), 2.int32)),
                elseBranch = null,
                isExhaustive = false
            ),
            CstIf("a".call)
        ).expressions
        "case\n1\nwhen 1\n2\nend\nif a\nend" becomes listOf(
            CstCase(
                1.int32,
                listOf(CstWhen(listOf(1.int32), 2.int32)),
                elseBranch = null,
                isExhaustive = false
            ),
            CstIf("a".call)
        ).expressions

        "case 1\nwhen .foo\n2\nend" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(CstCall(CstImplicitObj, "foo")), 2.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case 1\nwhen .responds_to?(:foo)\n2\nend" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(CstRespondsTo(CstImplicitObj, "foo")), 2.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case 1\nwhen .is_a?(T)\n2\nend" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(CstIsA(CstImplicitObj, "T".path)), 2.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case 1\nwhen .as(T)\n2\nend" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(CstCast(CstImplicitObj, "T".path)), 2.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case 1\nwhen .as?(T)\n2\nend" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(CstNilableCast(CstImplicitObj, "T".path)), 2.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case 1\nwhen .!()\n2\nend" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(CstNot(CstImplicitObj)), 2.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case when 1\n2\nend" becomes CstCase(
            null,
            listOf(CstWhen(listOf(1.int32), 2.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case \nwhen 1\n2\nend" becomes CstCase(
            null,
            listOf(CstWhen(listOf(1.int32), 2.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case {1, 2}\nwhen {3, 4}\n5\nend" becomes CstCase(
            CstTupleLiteral(listOf(1.int32, 2.int32)),
            listOf(CstWhen(listOf(CstTupleLiteral(listOf(3.int32, 4.int32))), 5.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case {1, 2}\nwhen {3, 4}, {5, 6}\n7\nend" becomes CstCase(
            CstTupleLiteral(listOf(1.int32, 2.int32)),
            listOf(
                CstWhen(
                    listOf(
                        CstTupleLiteral(listOf(3.int32, 4.int32)),
                        CstTupleLiteral(listOf(5.int32, 6.int32))
                    ),
                    7.int32
                )
            ),
            elseBranch = null,
            isExhaustive = false
        )
        "case {1, 2}\nwhen {.foo, .bar}\n5\nend" becomes CstCase(
            CstTupleLiteral(listOf(1.int32, 2.int32)),
            listOf(
                CstWhen(
                    listOf(
                        CstTupleLiteral(
                            listOf(
                                CstCall(CstImplicitObj, "foo"),
                                CstCall(CstImplicitObj, "bar")
                            )
                        )
                    ),
                    5.int32
                )
            ),
            elseBranch = null,
            isExhaustive = false
        )
        "case {1, 2}\nwhen foo\n5\nend" becomes CstCase(
            CstTupleLiteral(listOf(1.int32, 2.int32)),
            listOf(CstWhen(listOf("foo".call), 5.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case a\nwhen b\n1 / 2\nelse\n1 / 2\nend" becomes CstCase(
            "a".call,
            listOf(CstWhen(listOf("b".call), CstCall(1.int32, "/", 2.int32))),
            CstCall(1.int32, "/", 2.int32),
            isExhaustive = false
        )
        "case a\nwhen b\n/ /\n\nelse\n/ /\nend" becomes CstCase(
            "a".call,
            listOf(CstWhen(listOf("b".call), " ".regex)),
            " ".regex,
            isExhaustive = false
        )
        "case 1; end" becomes CstCase(
            1.int32,
            emptyList(),
            elseBranch = null,
            isExhaustive = false
        )
        "case foo; end" becomes CstCase(
            "foo".call,
            emptyList(),
            elseBranch = null,
            isExhaustive = false
        )
        "case\nend" becomes CstCase(
            null,
            emptyList(),
            elseBranch = null,
            isExhaustive = false
        )
        "case;end" becomes CstCase(
            null,
            emptyList(),
            elseBranch = null,
            isExhaustive = false
        )
        "case 1\nelse\n2\nend" becomes CstCase(
            1.int32,
            emptyList(),
            2.int32,
            isExhaustive = false
        )
        "a = 1\ncase 1\nwhen a then 1\nend" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstCase(
                1.int32,
                listOf(CstWhen(listOf("a".variable), 1.int32)),
                elseBranch = null,
                isExhaustive = false
            )
        ).expressions
        "case\nwhen true\n1\nend" becomes CstCase(
            null,
            listOf(CstWhen(listOf(true.bool), 1.int32)),
            elseBranch = null,
            isExhaustive = false
        )
        "case;when true;1;end" becomes CstCase(
            null,
            listOf(CstWhen(listOf(true.bool), 1.int32)),
            elseBranch = null,
            isExhaustive = false
        )

        "case 1\nin Int32; 2; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf("Int32".path), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case 1\nin Int32.class; 2; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(CstCall("Int32".path, "class")), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case 1\nin Foo(Int32); 2; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(CstGeneric("Foo".path, listOf("Int32".path))), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true)
        "case 1\nin false; 2; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(false.bool), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case 1\nin true; 2; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(true.bool), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case 1\nin nil; 2; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(nilLiteral), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case 1\nin .bar?; 2; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(CstCall(CstImplicitObj, "bar?")), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )

        "case {1}\nin {Int32}; 2; end" becomes CstCase(
            CstTupleLiteral(listOf(1.int32)),
            listOf(CstWhen(listOf(CstTupleLiteral(listOf("Int32".path))), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case {1}\nin {Int32.class}; 2; end" becomes CstCase(
            CstTupleLiteral(listOf(1.int32)),
            listOf(CstWhen(listOf(CstTupleLiteral(listOf(CstCall("Int32".path, "class")))), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case {1}\nin {Foo(Int32)}; 2; end" becomes CstCase(
            CstTupleLiteral(listOf(1.int32)),
            listOf(
                CstWhen(
                    listOf(CstTupleLiteral(listOf(CstGeneric("Foo".path, listOf("Int32".path))))),
                    2.int32,
                    true
                )
            ),
            elseBranch = null,
            isExhaustive = true
        )
        "case {1}\nin {false}; 2; end" becomes CstCase(
            CstTupleLiteral(listOf(1.int32)),
            listOf(CstWhen(listOf(CstTupleLiteral(listOf(false.bool))), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case {1}\nin {true}; 2; end" becomes CstCase(
            CstTupleLiteral(listOf(1.int32)),
            listOf(CstWhen(listOf(CstTupleLiteral(listOf(true.bool))), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case {1}\nin {nil}; 2; end" becomes CstCase(
            CstTupleLiteral(listOf(1.int32)),
            listOf(CstWhen(listOf(CstTupleLiteral(listOf(nilLiteral))), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case {1}\nin {.bar?}; 2; end" becomes CstCase(
            CstTupleLiteral(listOf(1.int32)),
            listOf(CstWhen(listOf(CstTupleLiteral(listOf(CstCall(CstImplicitObj, "bar?")))), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )
        "case {1}\nin {_}; 2; end" becomes CstCase(
            CstTupleLiteral(listOf(1.int32)),
            listOf(CstWhen(listOf(CstTupleLiteral(listOf(underscore))), 2.int32, true)),
            elseBranch = null,
            isExhaustive = true
        )

        "case 1; when 2 then /foo/; end" becomes CstCase(
            1.int32,
            listOf(CstWhen(listOf(2.int32), "foo".regex)),
            elseBranch = null,
            isExhaustive = false
        )

        "select\nwhen foo\n2\nend" becomes CstSelect(
            listOf(CstSelect.When("foo".call, 2.int32))
        )
        "select\nwhen foo\n2\nwhen bar\n4\nend" becomes CstSelect(
            listOf(
                CstSelect.When("foo".call, 2.int32),
                CstSelect.When("bar".call, 4.int32)
            )
        )
        "select\nwhen foo\n2\nelse\n3\nend" becomes CstSelect(
            listOf(CstSelect.When("foo".call, 2.int32)),
            3.int32
        )

        "def foo(x); end; x" becomes listOf(
            CstDef("foo", listOf("x".arg)),
            "x".call
        ).expressions
        "def foo; / /; end" becomes CstDef("foo", body = " ".regex)

        "\"foo#{bar}baz\"" becomes CstStringInterpolation(
            listOf("foo".string, "bar".call, "baz".string)
        )
        "qux \"foo#{bar do end}baz\"" becomes CstCall(
            null,
            "qux",
            CstStringInterpolation(
                listOf(
                    "foo".string,
                    CstCall(null, "bar", block = CstBlock.EMPTY),
                    "baz".string
                )
            )
        )
        "\"#{1\n}\"" becomes CstStringInterpolation(listOf(1.int32))

        "\"foo#{\"bar\"}baz\"" becomes "foobarbaz".string

        "lib LibFoo\nend\nif true\nend" becomes listOf(
            CstLibDef("LibFoo".path),
            CstIf(true.bool)
        ).expressions

        "foo(\n1\n)" becomes CstCall(null, "foo", 1.int32)

        "a = 1\nfoo - a" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstCall("foo".call, "-", "a".variable)
        ).expressions
        "a = 1\nfoo -a" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstCall(null, "foo", CstCall("a".variable, "-"))
        ).expressions

        "a : Foo" becomes CstTypeDeclaration(
            "a".variable,
            "Foo".path
        )
        "a : Foo | Int32" becomes CstTypeDeclaration(
            "a".variable,
            CstUnion(listOf("Foo".path, "Int32".path))
        )
        "@a : Foo" becomes CstTypeDeclaration(
            "@a".instanceVar,
            "Foo".path
        )
        "@a : Foo | Int32" becomes CstTypeDeclaration(
            "@a".instanceVar,
            CstUnion(listOf("Foo".path, "Int32".path))
        )
        "@@a : Foo" becomes CstTypeDeclaration(
            "@@a".classVar,
            "Foo".path
        )

        "a : Foo = 1" becomes CstTypeDeclaration("a".variable, "Foo".path, 1.int32)
        "@a : Foo = 1" becomes CstTypeDeclaration("@a".instanceVar, "Foo".path, 1.int32)
        "@@a : Foo = 1" becomes CstTypeDeclaration("@@a".classVar, "Foo".path, 1.int32)
    }

    fun testConversion2() {
        "Foo?" becomes CstGeneric(
            "Union".globalPath,
            listOf("Foo".path, "Nil".globalPath)
        )
        "a : Foo*" becomes CstTypeDeclaration(
            "a".variable,
            CstGeneric(
                "Pointer".globalPath,
                listOf("Foo".path)
            )
        )
        "a : Foo[12]" becomes CstTypeDeclaration(
            "a".variable,
            CstGeneric(
                "StaticArray".globalPath,
                listOf("Foo".path, 12.int32)
            )
        )

        "Foo()?" becomes CstGeneric(
            "Union".globalPath,
            listOf(
                CstGeneric("Foo".path, emptyList()),
                "Nil".globalPath
            )
        )
        "a : Foo()*" becomes CstTypeDeclaration(
            "a".variable,
            CstGeneric(
                "Pointer".globalPath,
                listOf(CstGeneric("Foo".path, emptyList()))
            )
        )
        "a : Foo()[12]" becomes CstTypeDeclaration(
            "a".variable,
            CstGeneric(
                "StaticArray".globalPath,
                listOf(CstGeneric("Foo".path, emptyList()), 12.int32)
            )
        )

        "a = uninitialized Foo; a" becomes listOf(
            CstUninitializedVar("a".variable, "Foo".path),
            "a".variable
        ).expressions
        "@a = uninitialized Foo" becomes CstUninitializedVar(
            "@a".instanceVar,
            "Foo".path
        )
        "@@a = uninitialized Foo" becomes CstUninitializedVar(
            "@@a".classVar,
            "Foo".path
        )

        "()" becomes CstExpressions(listOf(CstNop))
        "(1; 2; 3)" becomes CstExpressions(listOf(1.int32, 2.int32, 3.int32))

        "begin; rescue; end" becomes CstExceptionHandler(
            CstNop,
            listOf(CstRescue.EMPTY)
        )
        "begin; 1; rescue; 2; end" becomes CstExceptionHandler(
            1.int32,
            listOf(CstRescue(2.int32))
        )
        "begin; 1; ensure; 2; end" becomes CstExceptionHandler(
            1.int32,
            ensure = 2.int32
        )
        "begin\n1\nensure\n2\nend" becomes CstExceptionHandler(
            1.int32,
            ensure = 2.int32
        )
        "begin; 1; rescue Foo; 2; end" becomes CstExceptionHandler(
            1.int32,
            listOf(CstRescue(2.int32, listOf("Foo".path)))
        )
        "begin; 1; rescue ::Foo; 2; end" becomes CstExceptionHandler(
            1.int32,
            listOf(CstRescue(2.int32, listOf("Foo".globalPath)))
        )
        "begin; 1; rescue Foo | Bar; 2; end" becomes CstExceptionHandler(
            1.int32,
            listOf(CstRescue(2.int32, listOf("Foo".path, "Bar".path)))
        )
        "begin; 1; rescue ::Foo | ::Bar; 2; end" becomes CstExceptionHandler(
            1.int32,
            listOf(CstRescue(2.int32, listOf("Foo".globalPath, "Bar".globalPath)))
        )
        "begin; 1; rescue ex : Foo | Bar; 2; end" becomes CstExceptionHandler(
            1.int32,
            listOf(CstRescue(2.int32, listOf("Foo".path, "Bar".path), "ex"))
        )
        "begin; 1; rescue ex : ::Foo | ::Bar; 2; end" becomes CstExceptionHandler(
            1.int32,
            listOf(CstRescue(2.int32, listOf("Foo".globalPath, "Bar".globalPath), "ex"))
        )
        "begin; 1; rescue ex; 2; end" becomes CstExceptionHandler(
            1.int32,
            listOf(CstRescue(2.int32, emptyList(), "ex"))
        )
        "begin; 1; rescue; 2; else; 3; end" becomes CstExceptionHandler(
            1.int32,
            listOf(CstRescue(2.int32)),
            3.int32
        )
        "begin; 1; rescue ex; 2; end; ex" becomes listOf(
            CstExceptionHandler(
                1.int32,
                listOf(CstRescue(2.int32, emptyList(), "ex"))
            ),
            "ex".variable
        ).expressions

        "def foo(); 1; rescue; 2; end" becomes CstDef(
            "foo",
            body = CstExceptionHandler(
                1.int32,
                listOf(CstRescue(2.int32))
            )
        )

        "1.tap do; 1; rescue; 2; end" becomes CstCall(
            1.int32,
            "tap",
            block = CstBlock(body = CstExceptionHandler(1.int32, listOf(CstRescue(2.int32))))
        )
        "-> do; 1; rescue; 2; end" becomes CstProcLiteral(
            CstDef(
                "->",
                body = CstExceptionHandler(1.int32, listOf(CstRescue(2.int32)))
            )
        )
        "1.tap do |x|; 1; rescue; x; end" becomes CstCall(
            1.int32,
            "tap",
            block = CstBlock(
                listOf("x".variable),
                body = CstExceptionHandler(1.int32, listOf(CstRescue("x".variable)))
            )
        )

        "1 rescue 2" becomes CstExceptionHandler(
            1.int32,
            listOf(CstRescue(2.int32))
        )
        "x = 1 rescue 2" becomes CstAssign(
            "x".variable,
            CstExceptionHandler(1.int32, listOf(CstRescue(2.int32)))
        )
        "x = 1 ensure 2" becomes CstAssign(
            "x".variable,
            CstExceptionHandler(1.int32, ensure = 2.int32)
        )
        "a = 1; a rescue a" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstExceptionHandler("a".variable, listOf(CstRescue("a".variable)))
        ).expressions
        "a = 1; yield a rescue a" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstExceptionHandler(CstYield(listOf("a".variable)), listOf(CstRescue("a".variable)))
        ).expressions

        "1 ensure 2" becomes CstExceptionHandler(1.int32, ensure = 2.int32)
        "1 rescue 2" becomes CstExceptionHandler(1.int32, listOf(CstRescue(2.int32)))

        "foo ensure 2" becomes CstExceptionHandler("foo".call, ensure = 2.int32)
        "foo rescue 2" becomes CstExceptionHandler("foo".call, listOf(CstRescue(2.int32)))

        "a = 1; a ensure a" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstExceptionHandler("a".variable, ensure = "a".variable)
        ).expressions
        "a = 1; yield a ensure a" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstExceptionHandler(CstYield(listOf("a".variable)), ensure = "a".variable)
        ).expressions

        "1 <= 2 <= 3" becomes CstCall(CstCall(1.int32, "<=", 2.int32), "<=", 3.int32)
        "1 == 2 == 3 == 4" becomes CstCall(CstCall(CstCall(1.int32, "==", 2.int32), "==", 3.int32), "==", 4.int32)

        "-> do end" becomes CstProcLiteral.EMPTY
        "-> { }" becomes CstProcLiteral.EMPTY
        "->() { }" becomes CstProcLiteral.EMPTY
        "->(x : Int32) { }" becomes CstProcLiteral(
            CstDef("->", listOf("x".arg(restriction = "Int32".path)))
        )
        "->(x : Int32) { x }" becomes CstProcLiteral(
            CstDef("->", listOf("x".arg(restriction = "Int32".path)), "x".variable)
        )
        "->(x) { x }" becomes CstProcLiteral(
            CstDef("->", listOf("x".arg), "x".variable)
        )
        "x = 1; ->{ x }" becomes listOf(
            CstAssign("x".variable, 1.int32),
            CstProcLiteral(CstDef("->", body = "x".variable))
        ).expressions
        "f ->{ a do\n end\n }" becomes CstCall(
            null,
            "f",
            CstProcLiteral(CstDef("->", body = CstCall(null, "a", block = CstBlock.EMPTY)))
        )

        "-> : Int32 { }" becomes CstProcLiteral(CstDef("->", returnType = "Int32".path))
        "->\n:\nInt32\n{\n}" becomes CstProcLiteral(CstDef("->", returnType = "Int32".path))
        "->() : Int32 { }" becomes CstProcLiteral(CstDef("->", returnType = "Int32".path))
        "->() : Int32 do end" becomes CstProcLiteral(CstDef("->", returnType = "Int32".path))
        "->(x : Int32) : Int32 { }" becomes CstProcLiteral(
            CstDef("->", listOf("x".arg(restriction = "Int32".path)), returnType = "Int32".path)
        )

        listOf("foo", "foo=", "foo?", "foo!").forEach { method ->
            "->$method" becomes CstProcPointer(null, method)
            "foo = 1; ->foo.$method" becomes listOf(
                CstAssign("foo".variable, 1.int32),
                CstProcPointer("foo".variable, method)
            ).expressions
            "->Foo.$method" becomes CstProcPointer("Foo".path, method)
            "->@foo.$method" becomes CstProcPointer("@foo".instanceVar, method)
            "->@@foo.$method" becomes CstProcPointer("@@foo".classVar, method)
            "->::$method" becomes CstProcPointer(null, method, isGlobal = true)
            "->::Foo.$method" becomes CstProcPointer("Foo".globalPath, method)
        }

        "->Foo::Bar::Baz.foo" becomes CstProcPointer(
            listOf("Foo", "Bar", "Baz").path,
            "foo"
        )
        "->foo(Int32, Float64)" becomes CstProcPointer(
            null,
            "foo",
            listOf("Int32".path, "Float64".path)
        )
        "foo = 1; ->foo.bar(Int32)" becomes listOf(
            CstAssign("foo".variable, 1.int32),
            CstProcPointer("foo".variable, "bar", listOf("Int32".path))
        ).expressions
        "->foo(Void*)" becomes CstProcPointer(
            null,
            "foo",
            listOf("Void".path.pointerOf)
        )
        "call ->foo" becomes CstCall(
            null,
            "call",
            CstProcPointer(null, "foo")
        )
        "[] of ->\n" becomes CstArrayLiteral(elementType = CstProcNotation.EMPTY)

        "foo &->bar" becomes CstCall(null, "foo", blockArg = CstProcPointer(null, "bar"))

        "foo.bar = {} of Int32 => Int32" becomes CstCall(
            "foo".call,
            "bar=",
            CstHashLiteral(elementType = CstHashLiteral.Entry("Int32".path, "Int32".path))
        )

        "alias Foo = Bar" becomes CstAlias("Foo".path, "Bar".path)
        "alias Foo::Bar = Baz" becomes CstAlias(CstPath(listOf("Foo", "Bar")), "Baz".path)

        "def foo\n1\nend\nif 1\nend" becomes listOf(
            CstDef("foo", body = 1.int32),
            CstIf(1.int32)
        ).expressions

        "1.as Bar" becomes CstCast(1.int32, "Bar".path)
        "1.as(Bar)" becomes CstCast(1.int32, "Bar".path)
        "foo.as(Bar)" becomes CstCast("foo".call, "Bar".path)
        "foo.bar.as(Bar)" becomes CstCast(CstCall("foo".call, "bar"), "Bar".path)
        "call(foo.as Bar, Baz)" becomes CstCall(
            null,
            "call",
            args = listOf(CstCast("foo".call, "Bar".path), "Baz".path)
        )

        "as(Bar)" becomes CstCast("self".variable, "Bar".path)

        "1.as? Bar" becomes CstNilableCast(1.int32, "Bar".path)
        "1.as?(Bar)" becomes CstNilableCast(1.int32, "Bar".path)
        "as?(Bar)" becomes CstNilableCast("self".variable, "Bar".path)

        "typeof(1)" becomes CstTypeOf(listOf(1.int32))

        "typeof(a = 1); a" becomes listOf(
            CstTypeOf(listOf(CstAssign("a".variable, 1.int32))),
            "a".call
        ).expressions

        "puts ~1" becomes CstCall(null, "puts", CstCall(1.int32, "~"))

        "foo\n.bar" becomes CstCall("foo".call, "bar")
        "foo\n   .bar" becomes CstCall("foo".call, "bar")
        "foo\n\n  .bar" becomes CstCall("foo".call, "bar")
        "foo\n  #comment\n  .bar" becomes CstCall("foo".call, "bar")

        "{1}" becomes CstTupleLiteral(listOf(1.int32))
        "{1, 2, 3}" becomes CstTupleLiteral(listOf(1.int32, 2.int32, 3.int32))
        "{A::B}" becomes CstTupleLiteral(listOf(CstPath(listOf("A", "B"))))
        "{\n1,\n2\n}" becomes CstTupleLiteral(listOf(1.int32, 2.int32))
        "{\n1\n}" becomes CstTupleLiteral(listOf(1.int32))
        "{\n{1}\n}" becomes CstTupleLiteral(listOf(CstTupleLiteral(listOf(1.int32))))
        "{\"\".id}" becomes CstTupleLiteral(listOf(CstCall("".string, "id")))

        "foo { a = 1 }; a" becomes listOf(
            CstCall(null, "foo", block = CstBlock(body = CstAssign("a".variable, 1.int32))),
            "a".call
        ).expressions

        "foo.bar(1).baz" becomes CstCall(
            CstCall("foo".call, "bar", 1.int32),
            "baz"
        )

        "b.c ||= 1" becomes CstOpAssign(
            CstCall("b".call, "c"),
            "||",
            1.int32
        )
        "b.c &&= 1" becomes CstOpAssign(
            CstCall("b".call, "c"),
            "&&",
            1.int32
        )

        "a = 1; class Foo; @x = a; end" becomes listOf(
            CstAssign("a".variable, 1.int32),
            CstClassDef("Foo".path, CstAssign("@x".instanceVar, "a".call))
        ).expressions

        "@[Foo]" becomes CstAnnotation("Foo".path)
        "@[Foo()]" becomes CstAnnotation("Foo".path)
        "@[Foo(1)]" becomes CstAnnotation(
            "Foo".path,
            listOf(1.int32)
        )
        "@[Foo(\"hello\")]" becomes CstAnnotation(
            "Foo".path,
            listOf("hello".string)
        )
        "@[Foo(1, foo: 2)]" becomes CstAnnotation(
            "Foo".path,
            listOf(1.int32),
            listOf("foo".namedArg(2.int32))
        )
        "@[Foo(1, foo: 2\n)]" becomes CstAnnotation(
            "Foo".path,
            listOf(1.int32),
            listOf("foo".namedArg(2.int32))
        )
        "@[Foo(\n1, foo: 2\n)]" becomes CstAnnotation(
            "Foo".path,
            listOf(1.int32),
            listOf("foo".namedArg(2.int32))
        )
        "@[Foo::Bar]" becomes CstAnnotation(CstPath(listOf("Foo", "Bar")))

        "lib LibC\n@[Bar]; end" becomes CstLibDef(
            "LibC".path,
            CstAnnotation("Bar".path)
        )

        "Foo(_)" becomes CstGeneric(
            "Foo".path,
            listOf(underscore)
        )

        "{% if true %}\n{% end %}\n{% if true %}\n{% end %}" becomes listOf(
            CstMacroIf(true.bool, "\n".macroLiteral),
            CstMacroIf(true.bool, "\n".macroLiteral)
        ).expressions
        "fun foo : Int32; 1; end; 2" becomes listOf(
            CstFunDef("foo", returnType = "Int32".path, body = 1.int32),
            2.int32
        ).expressions

        "[] of ->;" becomes CstArrayLiteral(emptyList(), CstProcNotation.EMPTY)
        "[] of ->\n1" becomes listOf(
            CstArrayLiteral(emptyList(), CstProcNotation.EMPTY),
            1.int32
        ).expressions

        "def foo(x, *y); 1; end" becomes CstDef(
            "foo",
            listOf("x".arg, "y".arg),
            1.int32,
            splatIndex = 1
        )
        "macro foo(x, *y);end" becomes CstMacro(
            "foo",
            listOf("x".arg, "y".arg),
            body = CstExpressions.EMPTY,
            splatIndex = 1
        )

        "def foo(x = 1, *y); 1; end" becomes CstDef(
            "foo",
            listOf("x".arg(1.int32), "y".arg),
            1.int32,
            splatIndex = 1
        )
        "def foo(x, *y : Int32); 1; end" becomes CstDef(
            "foo",
            listOf("x".arg, "y".arg(restriction = "Int32".path)),
            1.int32,
            splatIndex = 1
        )
        "def foo(*y : *T); 1; end" becomes CstDef(
            "foo",
            listOf("y".arg(restriction = "T".path.splat)),
            1.int32,
            splatIndex = 0
        )

        "foo *bar" becomes CstCall(null, "foo", "bar".call.splat)
        "foo(*bar)" becomes CstCall(null, "foo", "bar".call.splat)
        "foo x, *bar" becomes CstCall(
            null,
            "foo",
            listOf("x".call, "bar".call.splat)
        )
        "foo(x, *bar, *baz, y)" becomes CstCall(
            null,
            "foo",
            listOf("x".call, "bar".call.splat, "baz".call.splat, "y".call)
        )
        "foo.bar=(baz)" becomes CstCall(
            "foo".call,
            "bar=",
            CstExpressions(listOf("baz".call))
        )
        "foo.bar=(*baz)" becomes CstCall(
            "foo".call,
            "bar=",
            "baz".call.splat
        )
        "foo.bar= *baz" becomes CstCall(
            "foo".call,
            "bar=",
            "baz".call.splat
        )
        "foo.bar = (1).abs" becomes CstCall(
            "foo".call,
            "bar=",
            CstCall(CstExpressions(listOf(1.int32)), "abs")
        )
        "foo[*baz]" becomes CstCall(
            "foo".call,
            "[]",
            "baz".call.splat
        )
        "foo[*baz] = 1" becomes CstCall(
            "foo".call,
            "[]=",
            listOf("baz".call.splat, 1.int32)
        )

        "foo **bar" becomes CstCall(null, "foo", CstDoubleSplat("bar".call))
        "foo(**bar)" becomes CstCall(null, "foo", CstDoubleSplat("bar".call))

        "foo 1, **bar" becomes CstCall(
            null,
            "foo",
            listOf(1.int32, CstDoubleSplat("bar".call))
        )
        "foo(1, **bar)" becomes CstCall(
            null,
            "foo",
            listOf(1.int32, CstDoubleSplat("bar".call))
        )

        "foo 1, **bar, &block" becomes CstCall(
            null,
            "foo",
            args = listOf(1.int32, CstDoubleSplat("bar".call)),
            blockArg = "block".call
        )
        "foo(1, **bar, &block)" becomes CstCall(
            null,
            "foo",
            args = listOf(1.int32, CstDoubleSplat("bar".call)),
            blockArg = "block".call
        )

        "private def foo; end" becomes CstVisibilityModifier(
            CrVisibility.PRIVATE,
            CstDef("foo")
        )
        "protected def foo; end" becomes CstVisibilityModifier(
            CrVisibility.PROTECTED,
            CstDef("foo")
        )

        "`foo`" becomes CstCall(null, "`", "foo".string)
        "`foo#{1}bar`" becomes CstCall(
            null,
            "`",
            CstStringInterpolation(listOf("foo".string, 1.int32, "bar".string))
        )
        "`foo\\``" becomes CstCall(null, "`", "foo`".string)
        "%x(`which(foo)`)" becomes CstCall(null, "`", "`which(foo)`".string)

        "def `(cmd); 1; end" becomes CstDef("`", listOf("cmd".arg), 1.int32)

        "def foo(bar = 1\n); 2; end" becomes CstDef(
            "foo",
            listOf("bar".arg(defaultValue = 1.int32)),
            2.int32
        )

        "Set {1, 2, 3}" becomes CstArrayLiteral(
            listOf(1.int32, 2.int32, 3.int32),
            receiverType = "Set".path
        )
        "Set() {1, 2, 3}" becomes CstArrayLiteral(
            listOf(1.int32, 2.int32, 3.int32),
            receiverType = CstGeneric("Set".path, emptyList())
        )
        "Set(Int32) {1, 2, 3}" becomes CstArrayLiteral(
            listOf(1.int32, 2.int32, 3.int32),
            receiverType = CstGeneric("Set".path, listOf("Int32".path))
        )

        "{*1}" becomes CstTupleLiteral(listOf(1.int32.splat))
        "{*1, 2}" becomes CstTupleLiteral(listOf(1.int32.splat, 2.int32))
        "{1, *2}" becomes CstTupleLiteral(listOf(1.int32, 2.int32.splat))
        "{*1, *2}" becomes CstTupleLiteral(listOf(1.int32.splat, 2.int32.splat))
        "{1, *2, 3, *4, 5}" becomes CstTupleLiteral(
            listOf(1.int32, 2.int32.splat, 3.int32, 4.int32.splat, 5.int32)
        )

        "[*1]" becomes CstArrayLiteral(listOf(1.int32.splat))
        "[*1, 2]" becomes CstArrayLiteral(listOf(1.int32.splat, 2.int32))
        "[1, *2]" becomes CstArrayLiteral(listOf(1.int32, 2.int32.splat))
        "[*1, *2]" becomes CstArrayLiteral(listOf(1.int32.splat, 2.int32.splat))
        "[1, *2, 3, *4, 5]" becomes CstArrayLiteral(listOf(1.int32, 2.int32.splat, 3.int32, 4.int32.splat, 5.int32))

        "Set {*1, 2, *3}" becomes CstArrayLiteral(
            listOf(1.int32.splat, 2.int32, 3.int32.splat),
            receiverType = "Set".path
        )

        "[*[*[1]], *[2]]" becomes CstArrayLiteral(
            listOf(
                CstArrayLiteral(listOf(CstArrayLiteral(listOf(1.int32)).splat)).splat,
                CstArrayLiteral(listOf(2.int32)).splat
            )
        )

        "case 1\nwhen {*2}; 3; end" becomes CstCase(
            1.int32,
            listOf(
                CstWhen(listOf(CstTupleLiteral(listOf(2.int32.splat))), 3.int32)
            ),
            elseBranch = null,
            isExhaustive = false
        )

        "x = {*1}" becomes CstAssign(
            "x".variable,
            CstTupleLiteral(listOf(1.int32.splat))
        )

        "{*1 * 2}" becomes CstTupleLiteral(
            listOf(CstCall(1.int32, "*", 2.int32).splat)
        )
        "[*1 ** 2]" becomes CstArrayLiteral(
            listOf(CstCall(1.int32, "**", 2.int32).splat)
        )
        "Set {*{1} * 2}" becomes CstArrayLiteral(
            listOf(
                CstCall(CstTupleLiteral(listOf(1.int32)), "*", 2.int32).splat
            ),
            receiverType = "Set".path
        )

        "foo(Bar) { 1 }" becomes CstCall(
            null,
            "foo",
            args = listOf("Bar".path),
            block = CstBlock(body = 1.int32)
        )
        "foo Bar { 1 }" becomes CstCall(
            null,
            "foo",
            args = listOf(CstArrayLiteral(listOf(1.int32), receiverType = "Bar".path))
        )
        "foo(Bar { 1 })" becomes CstCall(
            null,
            "foo",
            args = listOf(CstArrayLiteral(listOf(1.int32), receiverType = "Bar".path))
        )

        "1 \\\n + 2" becomes CstCall(1.int32, "+", 2.int32)
        "1\\\n + 2" becomes CstCall(1.int32, "+", 2.int32)
        "1 \\\r\n + 2" becomes CstCall(1.int32, "+", 2.int32)
        "1\\\r\n + 2" becomes CstCall(1.int32, "+", 2.int32)

        "\"hello \" \\\n \"world\"" becomes "hello world".string
        "\"hello \"\\\n\"world\"" becomes "hello world".string
        "\"hello \" \\\r\n \"world\"" becomes "hello world".string
        "\"hello \"\\\r\n\"world\"" becomes "hello world".string
        "\"hello #{1}\" \\\n \"#{2} world\"" becomes CstStringInterpolation(
            listOf("hello ".string, 1.int32, 2.int32, " world".string)
        )
        "\"hello #{1}\" \\\r\n \"#{2} world\"" becomes CstStringInterpolation(
            listOf("hello ".string, 1.int32, 2.int32, " world".string)
        )

        "enum Foo; A\nB; C\nD = 1; end" becomes CstEnumDef(
            "Foo".path,
            listOf("A".arg, "B".arg, "C".arg, "D".arg(1.int32))
        )
        "enum Foo; A = 1; B; end" becomes CstEnumDef(
            "Foo".path,
            listOf("A".arg(1.int32), "B".arg)
        )
        "enum Foo : UInt16; end" becomes CstEnumDef(
            "Foo".path,
            baseType = "UInt16".path
        )
        "enum Foo; def foo; 1; end; end" becomes CstEnumDef(
            "Foo".path,
            listOf(CstDef("foo", body = 1.int32))
        )
        "enum Foo; A = 1\ndef foo; 1; end; end" becomes CstEnumDef(
            "Foo".path,
            listOf("A".arg(1.int32), CstDef("foo", body = 1.int32))
        )
        "enum Foo; A = 1\ndef foo; 1; end\ndef bar; 2; end\nend" becomes CstEnumDef(
            "Foo".path,
            listOf(
                "A".arg(1.int32),
                CstDef("foo", body = 1.int32),
                CstDef("bar", body = 2.int32)
            )
        )
        "enum Foo; A = 1\ndef self.foo; 1; end\nend" becomes CstEnumDef(
            "Foo".path,
            listOf(
                "A".arg(1.int32),
                CstDef("foo", receiver = "self".variable, body = 1.int32)
            )
        )
        "enum Foo::Bar; A = 1; end" becomes CstEnumDef(
            CstPath(listOf("Foo", "Bar")),
            listOf("A".arg(1.int32))
        )

        "enum Foo; @@foo = 1\n A \n end" becomes CstEnumDef(
            "Foo".path,
            listOf(CstAssign("@@foo".classVar, 1.int32), "A".arg)
        )

        "enum Foo; private def foo; 1; end; end" becomes CstEnumDef(
            "Foo".path,
            listOf(
                CstVisibilityModifier(CrVisibility.PRIVATE, CstDef("foo", body = 1.int32))
            )
        )
        "enum Foo; protected def foo; 1; end; end" becomes CstEnumDef(
            "Foo".path,
            listOf(
                CstVisibilityModifier(CrVisibility.PROTECTED, CstDef("foo", body = 1.int32))
            )
        )

        "enum Foo; {{1}}; end" becomes CstEnumDef(
            "Foo".path,
            listOf(CstMacroExpression(1.int32))
        )
        "enum Foo; {% if 1 %}2{% end %}; end" becomes CstEnumDef(
            "Foo".path,
            listOf(CstMacroIf(1.int32, CstMacroLiteral("2")))
        )

        "enum Foo; macro foo;end; end" becomes CstEnumDef(
            "Foo".path,
            listOf(CstMacro("foo", emptyList(), CstExpressions.EMPTY))
        )

        "enum Foo; @[Bar]; end" becomes CstEnumDef(
            "Foo".path,
            listOf(CstAnnotation("Bar".path))
        )

        "1.[](2)" becomes CstCall(1.int32, "[]", 2.int32)
        "1.[]?(2)" becomes CstCall(1.int32, "[]?", 2.int32)
        "1.[]=(2, 3)" becomes CstCall(1.int32, "[]=", listOf(2.int32, 3.int32))

        "a @b-1\nc" becomes listOf(
            CstCall(null, "a", CstCall("@b".instanceVar, "-", 1.int32)),
            "c".call
        ).expressions
        "4./(2)" becomes CstCall(4.int32, "/", 2.int32)
        "foo[\n1\n]" becomes CstCall("foo".call, "[]", 1.int32)
        "foo[\nfoo[\n1\n]\n]" becomes CstCall(
            "foo".call,
            "[]",
            CstCall("foo".call, "[]", 1.int32)
        )

        "if (\ntrue\n)\n1\nend" becomes CstIf(CstExpressions(listOf(true.bool)), 1.int32)

        "my_def def foo\nloop do\nend\nend" becomes CstCall(
            null,
            "my_def",
            CstDef("foo", body = CstCall(null, "loop", block = CstBlock.EMPTY))
        )

        "foo(*{1})" becomes CstCall(null, "foo", CstTupleLiteral(listOf(1.int32)).splat)
        "foo *{1}" becomes CstCall(null, "foo", CstTupleLiteral(listOf(1.int32)).splat)

        "a.b/2" becomes CstCall(CstCall("a".call, "b"), "/", 2.int32)
        "a.b /2/" becomes CstCall("a".call, "b", "2".regex)
        "a.b / 2" becomes CstCall(CstCall("a".call, "b"), "/", 2.int32)
        "a/b" becomes CstCall("a".call, "/", "b".call)
        "T/1" becomes CstCall("T".path, "/", 1.int32)
        "T::U/1" becomes CstCall(CstPath(listOf("T", "U")), "/", 1.int32)
        "::T/1" becomes CstCall("T".globalPath, "/", 1.int32)

        "asm(\"nop\" \n)" becomes CstAsm("nop")
        "asm(\"nop\" : : )" becomes CstAsm("nop")
        "asm(\"nop\" ::)" becomes CstAsm("nop")
        "asm(\"nop\" :: : :)" becomes CstAsm("nop")
        "asm(\"nop\" ::: :)" becomes CstAsm("nop")
        "asm(\"nop\" ::::)" becomes CstAsm("nop")
        "asm(\"nop\" : \"a\"(0))" becomes CstAsm(
            "nop",
            listOf(CstAsmOperand("a", 0.int32))
        )
        "asm(\"nop\" : \"a\"(0) : \"b\"(1))" becomes CstAsm(
            "nop",
            listOf(CstAsmOperand("a", 0.int32)),
            listOf(CstAsmOperand("b", 1.int32))
        )
        "asm(\"nop\" : \"a\"(0) : \"b\"(1), \"c\"(2))" becomes CstAsm(
            "nop",
            listOf(CstAsmOperand("a", 0.int32)),
            listOf(CstAsmOperand("b", 1.int32), CstAsmOperand("c", 2.int32))
        )
        "asm(\"nop\" : \"a\"(0), \"b\"(1) : \"c\"(2), \"d\"(3))" becomes CstAsm(
            "nop",
            listOf(CstAsmOperand("a", 0.int32), CstAsmOperand("b", 1.int32)),
            listOf(CstAsmOperand("c", 2.int32), CstAsmOperand("d", 3.int32))
        )
        "asm(\"nop\" :: \"b\"(1), \"c\"(2))" becomes CstAsm(
            "nop",
            inputs = listOf(CstAsmOperand("b", 1.int32), CstAsmOperand("c", 2.int32))
        )
        "asm(\"nop\" :: \"b\"(1), \"c\"(2) ::)" becomes CstAsm(
            "nop",
            inputs = listOf(CstAsmOperand("b", 1.int32), CstAsmOperand("c", 2.int32))
        )
        "asm(\n\"nop\"\n:\n\"a\"(0)\n:\n\"b\"(1),\n\"c\"(2)\n)" becomes CstAsm(
            "nop",
            listOf(CstAsmOperand("a", 0.int32)),
            listOf(CstAsmOperand("b", 1.int32), CstAsmOperand("c", 2.int32))
        )
        "asm(\n\"nop\"\n:\n\"a\"(0),\n\"b\"(1)\n:\n\"c\"(2),\n\"d\"(3)\n)" becomes CstAsm(
            "nop",
            listOf(CstAsmOperand("a", 0.int32), CstAsmOperand("b", 1.int32)),
            listOf(CstAsmOperand("c", 2.int32), CstAsmOperand("d", 3.int32))
        )
        "asm(\"nop\" :: \"b\"(1), \"c\"(2) : \"eax\", \"ebx\" : \"volatile\", \"alignstack\", \"intel\")" becomes CstAsm(
            "nop",
            inputs = listOf(CstAsmOperand("b", 1.int32), CstAsmOperand("c", 2.int32)),
            clobbers = listOf("eax", "ebx"),
            volatile = true,
            alignStack = true,
            intel = true
        )
        "asm(\"nop\" :: \"b\"(1), \"c\"(2) : \"eax\", \"ebx\"\n: \"volatile\", \"alignstack\"\n,\n\"intel\"\n)" becomes CstAsm(
            "nop",
            inputs = listOf(CstAsmOperand("b", 1.int32), CstAsmOperand("c", 2.int32)),
            clobbers = listOf("eax", "ebx"),
            volatile = true,
            alignStack = true,
            intel = true
        )
        "asm(\"nop\" :::: \"volatile\")" becomes CstAsm(
            "nop",
            volatile = true
        )
        "asm(\"bl trap\" :::: \"unwind\")" becomes CstAsm(
            "bl trap",
            canThrow = true
        )

        "foo begin\nbar do\nend\nend" becomes CstCall(
            null,
            "foo",
            CstExpressions(listOf(CstCall(null, "bar", block = CstBlock.EMPTY)))
        )
        "foo 1.bar do\nend" becomes CstCall(
            null,
            "foo",
            args = listOf(CstCall(1.int32, "bar")),
            block = CstBlock.EMPTY
        )
        "return 1.bar do\nend" becomes CstReturn(
            CstCall(1.int32, "bar", block = CstBlock.EMPTY)
        )

        listOf(
            "begin", "nil", "true", "false", "yield", "with", "abstract", "def", "macro", "require", "case", "if",
            "unless", "include", "extend", "class", "struct", "module", "enum", "while", "until", "return", "next",
            "break", "lib", "fun", "alias", "pointerof", "sizeof", "instance_sizeof", "offsetof", "typeof", "private",
            "protected", "asm", "end", "do", "else", "elsif", "when", "rescue", "ensure"
        ).forEach { keyword ->
            "$keyword : Int32" becomes CstTypeDeclaration(keyword.variable, "Int32".path)
            "property $keyword : Int32" becomes CstCall(
                null,
                "property",
                CstTypeDeclaration(keyword.variable, "Int32".path)
            )
        }

        "call(foo : A, end : B)" becomes CstCall(
            null,
            "call",
            listOf(
                CstTypeDeclaration("foo".variable, "A".path),
                CstTypeDeclaration("end".variable, "B".path)
            )
        )
        "call foo : A, end : B" becomes CstCall(
            null,
            "call",
            listOf(
                CstTypeDeclaration("foo".variable, "A".path),
                CstTypeDeclaration("end".variable, "B".path)
            )
        )

        "case :foo; when :bar; 2; end" becomes CstCase(
            "foo".symbol,
            listOf(CstWhen(listOf("bar".symbol), 2.int32)),
            elseBranch = null,
            isExhaustive = false
        )

        "Foo.foo(count: 3).bar { }" becomes CstCall(
            CstCall(
                "Foo".path,
                "foo",
                namedArgs = listOf("count".namedArg(3.int32))
            ),
            "bar",
            block = CstBlock.EMPTY
        )

        """
        class Foo
          def bar
            print as Foo
          end
        end
        """ becomes CstClassDef(
            "Foo".path,
            CstDef(
                "bar",
                body = CstCall(null, "print", CstCast("self".variable, "Foo".path))
            )
        )

        "Foo?" becomes CstGeneric(
            "Union".globalPath,
            listOf("Foo".path, "Nil".globalPath)
        )
        "Foo::Bar?" becomes CstGeneric(
            "Union".globalPath,
            listOf(CstPath(listOf("Foo", "Bar")), "Nil".globalPath)
        )
        "Foo(T)?" becomes CstGeneric(
            "Union".globalPath,
            listOf(CstGeneric("Foo".path, listOf("T".path)), "Nil".globalPath)
        )
        "Foo??" becomes CstGeneric(
            "Union".globalPath,
            listOf(
                CstGeneric(
                    "Union".globalPath,
                    listOf("Foo".path, "Nil".globalPath)
                ),
                "Nil".globalPath
            )
        )

        "{1 => 2 / 3}" becomes CstHashLiteral(
            listOf(CstHashLiteral.Entry(1.int32, CstCall(2.int32, "/", 3.int32)))
        )
        "a { |x| x } / b" becomes CstCall(
            CstCall(null, "a", block = CstBlock(args = listOf("x".variable), body = "x".variable)),
            "/",
            "b".call
        )

        "1 if /x/" becomes CstIf("x".regex, 1.int32)

        "foo bar.baz(1) do\nend" becomes CstCall(
            null,
            "foo",
            args = listOf(CstCall("bar".call, "baz", 1.int32)),
            block = CstBlock.EMPTY
        )

        "1 rescue 2 if 3" becomes CstIf(3.int32, CstExceptionHandler(1.int32, listOf(CstRescue(2.int32))))
        "1 ensure 2 if 3" becomes CstIf(3.int32, CstExceptionHandler(1.int32, ensure = 2.int32))

        "yield foo do\nend" becomes CstYield(
            listOf(CstCall(null, "foo", block = CstBlock.EMPTY))
        )

        "x.y=(1).to_s" becomes CstCall(
            "x".call,
            "y=",
            CstCall(CstExpressions(listOf(1.int32)), "to_s")
        )

        "1 ** -x" becomes CstCall(1.int32, "**", CstCall("x".call, "-"))

        "foo.Bar" becomes CstCall("foo".call, "Bar")

        listOf('(' to ')', '[' to ']', '<' to '>', '{' to '}', '|' to '|').forEach { (open, close) ->
            "{% begin %}%$open %s $close{% end %}" becomes CstMacroIf(
                true.bool,
                "%$open %s $close".macroLiteral
            )
            "{% begin %}%q$open %s $close{% end %}" becomes CstMacroIf(
                true.bool,
                "%q$open %s $close".macroLiteral
            )
            "{% begin %}%Q$open %s $close{% end %}" becomes CstMacroIf(
                true.bool,
                "%Q$open %s $close".macroLiteral
            )
            "{% begin %}%i$open %s $close{% end %}" becomes CstMacroIf(
                true.bool,
                "%i$open %s $close".macroLiteral
            )
            "{% begin %}%w$open %s $close{% end %}" becomes CstMacroIf(
                true.bool,
                "%w$open %s $close".macroLiteral
            )
            "{% begin %}%x$open %s $close{% end %}" becomes CstMacroIf(
                true.bool,
                CstMacroLiteral("%x$open %s $close")
            )
            "{% begin %}%r$open\\A$close{% end %}" becomes CstMacroIf(
                true.bool,
                CstMacroLiteral("%r$open\\A$close")
            )
        }

        "foo(bar:\"a\", baz:\"b\")" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("bar".namedArg("a".string), "baz".namedArg("b".string))
        )
        "foo(bar:a, baz:b)" becomes CstCall(
            null,
            "foo",
            namedArgs = listOf("bar".namedArg("a".call), "baz".namedArg("b".call))
        )
        "{foo:\"a\", bar:\"b\"}" becomes CstNamedTupleLiteral(
            listOf(
                CstNamedTupleLiteral.Entry("foo", "a".string),
                CstNamedTupleLiteral.Entry("bar", "b".string)
            )
        )
        "{foo:'a', bar:'b'}" becomes CstNamedTupleLiteral(
            listOf(
                CstNamedTupleLiteral.Entry("foo", 'a'.char),
                CstNamedTupleLiteral.Entry("bar", 'b'.char)
            )
        )
        "{foo:a, bar:b}" becomes CstNamedTupleLiteral(
            listOf(
                CstNamedTupleLiteral.Entry("foo", "a".call),
                CstNamedTupleLiteral.Entry("bar", "b".call)
            )
        )

        "[\n1\n]" becomes CstArrayLiteral(listOf(1.int32))
        "[\n1,2\n]" becomes CstArrayLiteral(listOf(1.int32, 2.int32))

        "{[] of Foo, Bar::Baz.new}" becomes CstTupleLiteral(
            listOf(
                CstArrayLiteral(emptyList(), "Foo".path),
                CstCall(CstPath(listOf("Bar", "Baz")), "new")
            )
        )
        "{[] of Foo, ::Bar::Baz.new}" becomes CstTupleLiteral(
            listOf(
                CstArrayLiteral(emptyList(), "Foo".path),
                CstCall(CstPath(listOf("Bar", "Baz"), isGlobal = true), "new")
            )
        )
        "{[] of Foo, Bar::Baz + 2}" becomes CstTupleLiteral(
            listOf(
                CstArrayLiteral(emptyList(), "Foo".path),
                CstCall(CstPath(listOf("Bar", "Baz")), "+", listOf(2.int32))
            )
        )
        "{[] of Foo, Bar::Baz * 2}" becomes CstTupleLiteral(
            listOf(
                CstArrayLiteral(emptyList(), "Foo".path),
                CstCall(CstPath(listOf("Bar", "Baz")), "*", listOf(2.int32))
            )
        )
        "{[] of Foo, Bar::Baz ** 2}" becomes CstTupleLiteral(
            listOf(
                CstArrayLiteral(emptyList(), "Foo".path),
                CstCall(CstPath(listOf("Bar", "Baz")), "**", listOf(2.int32))
            )
        )
        "{[] of Foo, ::foo}" becomes CstTupleLiteral(
            listOf(
                CstArrayLiteral(emptyList(), "Foo".path),
                CstCall(null, "foo", isGlobal = true)
            )
        )
        "{[] of Foo, self.foo}" becomes CstTupleLiteral(
            listOf(
                CstArrayLiteral(emptyList(), "Foo".path),
                CstCall("self".variable, "foo")
            )
        )

        "macro foo; bar class: 1; end" becomes CstMacro(
            "foo",
            body = CstMacroLiteral(" bar class: 1; ")
        )

        "%w{one  two}" becomes listOf("one".string, "two".string).arrayOf("String".globalPath)
        "%w{one\ntwo}" becomes listOf("one".string, "two".string).arrayOf("String".globalPath)
        "%w{one\ttwo}" becomes listOf("one".string, "two".string).arrayOf("String".globalPath)
        "%w{\n}" becomes emptyList<CstNode>().arrayOf("String".globalPath)
        "%w{one\\ two}" becomes listOf("one two".string).arrayOf("String".globalPath)
        "%w{one{} two}" becomes listOf("one{}".string, "two".string).arrayOf("String".globalPath)
        "%w{\\{one}" becomes listOf("{one".string).arrayOf("String".globalPath)
        "%w{one\\}}" becomes listOf("one}".string).arrayOf("String".globalPath)
        "%i(one\\ two)" becomes listOf("one two".symbol).arrayOf("Symbol".globalPath)
        "%i{(one two)}" becomes listOf("(one".symbol, "two)".symbol).arrayOf("Symbol".globalPath)
        "%i((one two))" becomes listOf("(one".symbol, "two)".symbol).arrayOf("Symbol".globalPath)
        "%i(foo(bar) baz)" becomes listOf("foo(bar)".symbol, "baz".symbol).arrayOf("Symbol".globalPath)
        "%i{foo\\nbar baz}" becomes listOf("foo\\nbar".symbol, "baz".symbol).arrayOf("Symbol".globalPath)

        "annotation Foo; end" becomes CstAnnotationDef("Foo".path)
        "annotation Foo\n\nend" becomes CstAnnotationDef("Foo".path)
        "annotation Foo::Bar\n\nend" becomes CstAnnotationDef(CstPath(listOf("Foo", "Bar")))

        "annotation Foo\nend\nrequire \"bar\"" becomes listOf(
            CstAnnotationDef("Foo".path),
            CstRequire("bar")
        ).expressions

        "def foo(x = __LINE__); end" becomes CstDef(
            "foo",
            args = listOf("x".arg(defaultValue = magicLine))
        )
        "def foo(x = __FILE__); end" becomes CstDef(
            "foo",
            args = listOf("x".arg(defaultValue = magicFile))
        )
        "def foo(x = __DIR__); end" becomes CstDef(
            "foo",
            args = listOf("x".arg(defaultValue = magicDir))
        )

        "macro foo(x = __LINE__);end" becomes CstMacro(
            "foo",
            body = CstExpressions.EMPTY,
            args = listOf("x".arg(defaultValue = magicLine))
        )

        """ 
        macro foo
          <<-FOO
            #{ %var }
          FOO
        end
        """.trimIndent() becomes CstMacro(
            "foo",
            body = CstExpressions(
                listOf(
                    "  <<-FOO\n    #{ ".macroLiteral,
                    "var".macroVar,
                    " }\n  FOO\n".macroLiteral
                )
            )
        )

        """
        macro foo
          <<-FOO, <<-BAR + ""
          FOO
          BAR
        end    
        """.trimIndent() becomes CstMacro(
            "foo",
            body = "  <<-FOO, <<-BAR + \"\"\n  FOO\n  BAR\n".macroLiteral
        )

        """
        macro foo
          <<-FOO
            %foo
          FOO
        end   
        """.trimIndent() becomes CstMacro(
            "foo",
            body = "  <<-FOO\n    %foo\n  FOO\n".macroLiteral
        )

        "<<-HERE\nHello, mom! I am HERE.\nHER dress is beautiful.\nHE is OK.\n  HERESY\nHERE" becomes
                "Hello, mom! I am HERE.\nHER dress is beautiful.\nHE is OK.\n  HERESY".stringInterpolation
        "<<-HERE\n   One\n  Zero\n  HERE" becomes " One\nZero".stringInterpolation
        "<<-HERE\n   One \\n Two\n  Zero\n  HERE" becomes " One \n Two\nZero".stringInterpolation
        "<<-HERE\n   One\n\n  Zero\n  HERE" becomes " One\n\nZero".stringInterpolation
        "<<-HERE\n   One\n \n  Zero\n  HERE" becomes " One\n\nZero".stringInterpolation
        "<<-HERE\n   #{1}One\n  #{2}Zero\n  HERE" becomes CstStringInterpolation(
            listOf(" ".string, 1.int32, "One\n".string, 2.int32, "Zero".string)
        )
        "<<-HERE\n  foo#{1}bar\n   baz\n  HERE" becomes CstStringInterpolation(
            listOf("foo".string, 1.int32, "bar\n baz".string)
        )
        "<<-HERE\r\n   One\r\n  Zero\r\n  HERE" becomes " One\r\nZero".stringInterpolation
        "<<-HERE\r\n   One\r\n  Zero\r\n  HERE\r\n" becomes " One\r\nZero".stringInterpolation
        "<<-SOME\n  Sa\n  Se\n  SOME" becomes "Sa\nSe".stringInterpolation
        "<<-HERE\n  #{1} #{2}\n  HERE" becomes CstStringInterpolation(
            listOf(1.int32, " ".string, 2.int32)
        )
        "<<-HERE\n  #{1} \\n #{2}\n  HERE" becomes CstStringInterpolation(
            listOf(1.int32, " \n ".string, 2.int32)
        )
        "<<-HERE\nHERE" becomes "".stringInterpolation
        "<<-HERE1; <<-HERE2\nHERE1\nHERE2" becomes listOf(
            "".stringInterpolation,
            "".stringInterpolation
        ).expressions
        "<<-HERE1; <<-HERE2\nhere1\nHERE1\nHERE2" becomes listOf(
            "here1".stringInterpolation,
            "".stringInterpolation
        ).expressions
        "<<-HERE1; <<-HERE2\nHERE1\nhere2\nHERE2" becomes listOf(
            "".stringInterpolation,
            "here2".stringInterpolation
        ).expressions

        "<<-'HERE'\n  hello \\n world\n  #{1}\n  HERE" becomes "hello \\n world\n#{1}".stringInterpolation

        "<<-'HERE COMES HEREDOC'\n  hello \\n world\n  #{1}\n  HERE COMES HEREDOC" becomes
                "hello \\n world\n#{1}".stringInterpolation

        "<<-EOF.x\n  foo\nEOF" becomes CstCall("  foo".stringInterpolation, "x")
        "<<-'EOF'.x\n  foo\nEOF" becomes CstCall("  foo".stringInterpolation, "x")

        "<<-FOO\n\t1\n\tFOO" becomes "1".stringInterpolation
        "<<-FOO\n \t1\n \tFOO" becomes "1".stringInterpolation
        "<<-FOO\n \t 1\n \t FOO" becomes "1".stringInterpolation
        "<<-FOO\n\t 1\n\t FOO" becomes "1".stringInterpolation

        "x, y = <<-FOO, <<-BAR\nhello\nFOO\nworld\nBAR" becomes CstMultiAssign(
            listOf("x".variable, "y".variable),
            listOf("hello".stringInterpolation, "world".stringInterpolation)
        )

        "x, y, z = <<-FOO, <<-BAR, <<-BAZ\nhello\nFOO\nworld\nBAR\n!\nBAZ" becomes CstMultiAssign(
            listOf("x".variable, "y".variable, "z".variable),
            listOf("hello".stringInterpolation, "world".stringInterpolation, "!".stringInterpolation)
        )

        "foo(&.block)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstCall("__arg0".variable, "block"))
        )
        "foo &.block" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstCall("__arg0".variable, "block"))
        )
        "foo &./(1)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstCall("__arg0".variable, "/", 1.int32))
        )
        "foo &.%(1)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstCall("__arg0".variable, "%", 1.int32))
        )
        "foo &.block(1)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall("__arg0".variable, "block", 1.int32)
            )
        )
        "foo &.+(2)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall("__arg0".variable, "+", 2.int32)
            )
        )
        "foo &.bar.baz" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall(CstCall("__arg0".variable, "bar"), "baz")
            )
        )
        "foo(&.bar.baz)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall(CstCall("__arg0".variable, "bar"), "baz")
            )
        )
        "foo &.block[0]" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall(CstCall("__arg0".variable, "block"), "[]", 0.int32)
            )
        )
        "foo &.block=(0)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall("__arg0".variable, "block=", 0.int32)
            )
        )
        "foo &.block = 0" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall("__arg0".variable, "block=", 0.int32)
            )
        )
        "foo &.block[0] = 1" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall(CstCall("__arg0".variable, "block"), "[]=", listOf(0.int32, 1.int32))
            )
        )
        "foo &.[0]" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall("__arg0".variable, "[]", 0.int32)
            )
        )
        "foo &.[0] = 1" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall("__arg0".variable, "[]=", listOf(0.int32, 1.int32))
            )
        )
        "foo(&.is_a?(T))" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstIsA("__arg0".variable, "T".path))
        )
        "foo(&.!)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstNot("__arg0".variable))
        )
        "foo(&.responds_to?(:foo))" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstRespondsTo("__arg0".variable, "foo"))
        )
        "foo &.each {\n}" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstCall("__arg0".variable, "each", block = CstBlock.EMPTY))
        )
        "foo &.each do\nend" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstCall("__arg0".variable, "each", block = CstBlock.EMPTY))
        )
        "foo &.@bar" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstReadInstanceVar("__arg0".variable, "@bar"))
        )

        "foo(&.as(T))" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCast("__arg0".variable, "T".path)
            )
        )
        "foo(&.as(T).bar)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall(CstCast("__arg0".variable, "T".path), "bar")
            )
        )
        "foo &.as(T)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCast("__arg0".variable, "T".path)
            )
        )
        "foo &.as(T).bar" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall(CstCast("__arg0".variable, "T".path), "bar")
            )
        )

        "foo(&.as?(T))" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstNilableCast("__arg0".variable, "T".path)
            )
        )
        "foo(&.as?(T).bar)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall(CstNilableCast("__arg0".variable, "T".path), "bar")
            )
        )
        "foo &.as?(T)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstNilableCast("__arg0".variable, "T".path)
            )
        )
        "foo &.as?(T).bar" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("__arg0".variable),
                CstCall(CstNilableCast("__arg0".variable, "T".path), "bar")
            )
        )
        "foo(\n  &.block\n)" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstCall("__arg0".variable, "block"))
        )

        "foo &.!" becomes CstCall(
            null,
            "foo",
            block = CstBlock(listOf("__arg0".variable), CstNot("__arg0".variable))
        )

        "foo { |a, (b, c), (d, e)| a; b; c; d; e }" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("a".variable, "".variable, "".variable),
                CstExpressions(
                    listOf("a".variable, "b".variable, "c".variable, "d".variable, "e".variable)
                ),
                unpacks = Int2ObjectOpenHashMap<CstExpressions>().apply {
                    put(1, CstExpressions(listOf("b".variable, "c".variable)))
                    put(2,  CstExpressions(listOf("d".variable, "e".variable)))
                }
            )
        )

        "foo { |(_, c)| c }" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("".variable),
                "c".variable,
                unpacks = Int2ObjectOpenHashMap<CstExpressions>().apply {
                    put(0, CstExpressions(listOf(underscore, "c".variable)))
                }
            )
        )

        "foo { |(_, c, )| c }" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("".variable),
                "c".variable,
                unpacks = Int2ObjectOpenHashMap<CstExpressions>().apply {
                    put(0,  CstExpressions(listOf(underscore, "c".variable)))
                }
            )
        )

        "foo { |(a, (b, (c, d)))| }" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("".variable),
                CstNop,
                unpacks = Int2ObjectOpenHashMap<CstExpressions>().apply {
                    put(
                        0,
                        CstExpressions(
                            listOf(
                                "a".variable,
                                CstExpressions(
                                    listOf(
                                        "b".variable,
                                        CstExpressions(
                                            listOf("c".variable, "d".variable)
                                        ),
                                    ),
                                )
                            )
                        )
                    )
                },
            )
        )

        "foo { |(a, *b, c)| }" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("".variable),
                CstNop,
                unpacks = Int2ObjectOpenHashMap<CstExpressions>().apply {
                    put(
                        0,
                        CstExpressions(
                            listOf("a".variable, "b".variable.splat, "c".variable),
                        )
                    )
                }
            ),
        )

        "foo { |(a, *_, b)| }" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("".variable),
                CstNop,
                unpacks = Int2ObjectOpenHashMap<CstExpressions>().apply {
                    put(
                        0,
                        CstExpressions(
                            listOf("a".variable, underscore.splat, "b".variable),
                        )
                    )
                }
            ),
        )

        "foo { |(a, *(x, y), b)| }" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("".variable),
                CstNop,
                unpacks = Int2ObjectOpenHashMap<CstExpressions>().apply {
                    put(
                        0,
                        CstExpressions(
                            listOf(
                                "a".variable,
                                CstExpressions(listOf("x".variable, "y".variable)).splat,
                                "b".variable
                            ),
                        )
                    )
                }
            ),
        )

        "foo { |x, *(a, b, c)| }" becomes CstCall(
            null,
            "foo",
            block = CstBlock(
                listOf("x".variable, "".variable),
                CstNop,
                splatIndex = 1,
                unpacks = Int2ObjectOpenHashMap<CstExpressions>().apply {
                    put(
                        1,
                        CstExpressions(
                            listOf("a".variable, "b".variable, "c".variable),
                        )
                    )
                }
            ),
        )
    }

    fun testBlockUnpacking19() {
        project.withLanguageLevel(CrystalLevel.CRYSTAL_1_9) {
            "foo { |a, (b, c), (d, e)| a; b; c; d; e }" becomes CstCall(
                null,
                "foo",
                block = CstBlock(
                    listOf("a".variable, "__arg0".variable, "__arg1".variable),
                    listOf(
                        CstAssign("b".variable, CstCall("__arg0".variable, "[]", 0.int32)),
                        CstAssign("c".variable, CstCall("__arg0".variable, "[]", 1.int32)),
                        CstAssign("d".variable, CstCall("__arg1".variable, "[]", 0.int32)),
                        CstAssign("e".variable, CstCall("__arg1".variable, "[]", 1.int32)),
                        "a".variable, "b".variable, "c".variable, "d".variable, "e".variable
                    ).expressions
                )
            )

            "foo { |(_, c)| c }" becomes CstCall(
                null,
                "foo",
                block = CstBlock(
                    listOf("__arg0".variable),
                    listOf(
                        CstAssign("c".variable, CstCall("__arg0".variable, "[]", 1.int32)),
                        "c".variable,
                    ).expressions
                )
            )
        }
    }
}

private val falseLiteral: CstBoolLiteral = CstBoolLiteral.False(null)

private val trueLiteral: CstBoolLiteral = CstBoolLiteral.True(null)

private val Boolean.bool: CstBoolLiteral
    get() = if (this) trueLiteral else falseLiteral

private fun Number.numberLiteral(kind: CstNumberLiteral.NumberKind) =
    CstNumberLiteral(toString(), kind)

private val Number.int32: CstNumberLiteral
    get() = numberLiteral(CstNumberLiteral.NumberKind.I32)

private val Number.int64: CstNumberLiteral
    get() = numberLiteral(CstNumberLiteral.NumberKind.I64)

private val Number.int128: CstNumberLiteral
    get() = numberLiteral(CstNumberLiteral.NumberKind.I128)

private val Number.uint128: CstNumberLiteral
    get() = numberLiteral(CstNumberLiteral.NumberKind.U128)

private val Number.float32: CstNumberLiteral
    get() = CstNumberLiteral(toFloat().toString(), CstNumberLiteral.NumberKind.F32)

private val Number.float64: CstNumberLiteral
    get() = CstNumberLiteral(toDouble().toString(), CstNumberLiteral.NumberKind.F64)

private val Char.char: CstCharLiteral
    get() = CstCharLiteral(code)

private val String.string: CstStringLiteral
    get() = CstStringLiteral(this)

private val String.symbol: CstSymbolLiteral
    get() = CstSymbolLiteral(this)

private val String.regex: CstRegexLiteral
    get() = CstRegexLiteral(CstStringLiteral(this))

private fun String.regex(options: Int = CstRegexLiteral.NONE) =
    CstRegexLiteral(CstStringLiteral(this), options)

private val String.variable: CstVar
    get() = CstVar(this)

private val String.instanceVar: CstInstanceVar
    get() = CstInstanceVar(this)

private val String.classVar: CstClassVar
    get() = CstClassVar(this)

private val String.call: CstCall
    get() = CstCall(null, this)

private val String.globalCall: CstCall
    get() = CstCall(null, this, isGlobal = true)

private fun String.call(arg: CstNode): CstCall =
    CstCall(null, this, listOf(arg))

private fun String.call(arg1: CstNode, arg2: CstNode): CstCall =
    CstCall(null, this, listOf(arg1, arg2))

private val String.arg: CstArg
    get() = CstArg(this)

private fun String.arg(
    defaultValue: CstNode? = null,
    restriction: CstNode? = null,
    externalName: String? = null,
    annotations: List<CstAnnotation> = emptyList()
): CstArg = CstArg(
    this,
    defaultValue = defaultValue,
    restriction = restriction,
    externalName = externalName,
    annotations = annotations
)

private val String.path: CstPath
    get() = CstPath(listOf(this), false)

private val String.globalPath: CstPath
    get() = CstPath(listOf(this), true)

private fun String.staticArrayOf(size: Int): CstGeneric =
    staticArrayOf(size.int32)

private fun String.staticArrayOf(size: CstNode): CstGeneric =
    CstGeneric("StaticArray".globalPath, listOf(path, size))

private val String.ann: CstAnnotation
    get() = CstAnnotation(path)

private val String.macroLiteral: CstMacroLiteral
    get() = CstMacroLiteral(this)

private fun String.namedArg(value: CstNode): CstNamedArgument =
    CstNamedArgument(this, value)

private val String.macroVar: CstMacroVar
    get() = CstMacroVar(this)

private fun String.macroVar(exps: List<CstNode> = emptyList()): CstMacroVar =
    CstMacroVar(this, exps)

private val String.stringInterpolation: CstStringInterpolation
    get() = CstStringInterpolation(listOf(string))

private val List<String>.path: CstPath
    get() = CstPath(this, false)

private val CstNode.splat: CstSplat
    get() = CstSplat(this)

private val CstNode.pointerOf: CstGeneric
    get() = CstGeneric("Pointer".globalPath, listOf(this))

private val CstNode.typeOf: CstTypeOf
    get() = CstTypeOf(listOf(this))

private val CstNode.regex: CstRegexLiteral
    get() = CstRegexLiteral(this)

private val List<CstNode>.array: CstArrayLiteral
    get() = CstArrayLiteral(this)

private fun List<CstNode>.arrayOf(type: CstNode): CstArrayLiteral =
    CstArrayLiteral(this, type)

private val List<CstNode>.expressions: CstNode
    get() = CstExpressions.from(this)

private val self: CstSelf = CstSelf()

private val underscore: CstUnderscore = CstUnderscore()

private val magicDir: CstMagicConstant = CstMagicConstant.Dir()

private val magicFile: CstMagicConstant = CstMagicConstant.File()

private val magicLine: CstMagicConstant = CstMagicConstant.Line()

private val nilLiteral: CstNilLiteral = CstNilLiteral()
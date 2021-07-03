package org.crystal.intellij.tests

import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.hasErrorElementInRange
import com.intellij.testFramework.ParsingTestCase
import junit.framework.TestCase
import org.crystal.intellij.lexer.*
import org.crystal.intellij.parser.CrystalParserDefinition
import java.io.File

class CrystalParserTest : ParsingTestCase("parser", "cr", CrystalParserDefinition()) {
    override fun getTestDataPath(): String = File("src/testData").absolutePath

    private fun doTestError(code: String) {
        myFile = createPsiFile("a", code)
        ensureParsed(myFile)
        
        TestCase.assertTrue(myFile.text, myFile.hasErrorElementInRange(myFile.textRange))
    }
    
    private fun doCustomTest(code: String, adjustTestData: String.() -> String = { this }) {
        myFile = createPsiFile("a", code)
        ensureParsed(myFile)

        assertEquals(code, myFile.text)

        val expectedTree = loadFile("$testName.txt").adjustTestData()
        val actualTree = toParseTreeText(myFile, skipSpaces(), includeRanges()).trim { it <= ' ' }
        TestCase.assertEquals(expectedTree, actualTree)
    }

    private fun doTestSingleFileWithMultiFragments(delimiter: String = "\n\n") {
        doTestMultiFragments(loadFile("$testName.cr").splitToSequence(delimiter))
    }

    private fun doTestMultiFragments(vararg fragments: String) {
        doTestMultiFragments(sequenceOf(*fragments))
    }
    
    private fun doTestMultiFragments(fragments: Sequence<String>) {
        val name = testName
        val actualTree = StringBuilder("FILE")
        var firstFragment = true
        fragments.forEach { fragment ->
            myFile = createPsiFile("a", fragment)
            ensureParsed(myFile)

            assertEquals(fragment, myFile.text)

            if (firstFragment) {
                firstFragment = false
            }
            else {
                actualTree.append("\n")
            }

            for (child in myFile.children) {
                actualTree.append("\n")
                val fragmentTree = toParseTreeText(child, skipSpaces(), includeRanges()).trim { it <= ' ' }
                actualTree.append(fragmentTree.prependIndent("  "))
            }
        }

        doCheckResult(myFullDataPath, "$name.txt", actualTree.toString())
    }

    fun testAnnotations() = doTestSingleFileWithMultiFragments()
    fun testArrayLiterals() = doTestSingleFileWithMultiFragments()
    fun testAssignments() = doTestSingleFileWithMultiFragments()
    fun testCallsAndRefs() = doTestSingleFileWithMultiFragments("\n\n\n")
    fun testCaseSelect() = doTestSingleFileWithMultiFragments()
    fun testCharLiterals() = doTestSingleFileWithMultiFragments()
    fun testControlExpressions() = doTestSingleFileWithMultiFragments()
    fun testDefs() = doTestSingleFileWithMultiFragments()
    fun testExceptionHandlers() = doTestSingleFileWithMultiFragments()
    fun testFunctionLiterals() = doTestSingleFileWithMultiFragments()
    fun testGenerics() = doTestSingleFileWithMultiFragments()
    fun testHashesAndTuples() = doTestSingleFileWithMultiFragments()
    fun testLibraries() = doTestSingleFileWithMultiFragments()
    fun testMisc() = doTestSingleFileWithMultiFragments()
    fun testOperators() = doTestSingleFileWithMultiFragments()
    fun testPseudoConstants() = doTestSingleFileWithMultiFragments()
    fun testPseudoMethods() = doTestSingleFileWithMultiFragments()
    fun testSimpleLiterals() = doTestSingleFileWithMultiFragments()
    fun testStringLiterals() = doTestSingleFileWithMultiFragments("\n\n\n")
    fun testSymbols() = doTestSingleFileWithMultiFragments()
    fun testTypes() = doTestSingleFileWithMultiFragments()
    fun testTypeDefs() = doTestSingleFileWithMultiFragments("\n\n\n")

    // Custom tests

    fun testSpaces() {
        doTestMultiFragments(
            "",
            " ",
            "\t",
            " \t ",
            "\t \t",
            "\n",
            "\n\n\n",
            " \n\n"
        )
    }

    fun testComments() {
        doTestMultiFragments(
            "1 #",
            "1 #{",
            "1 # sajdfhs 1 ''",
            "1 # #"
        )
    }

    fun testIntegers() {
        doTestMultiFragments(
            sequence {
                yield("1")
                yield("1hello")
                yield("+1")
                yield("-1")
                yield("1234")
                yield("+1234")
                yield("-1234")
                yield("1.foo")

                yield("1_000")
                yield("100_000")
                yield("10_0_00_0")

                yield("0b1010")
                yield("0b_101_0")
                yield("+0b1010")
                yield("-0b1010")

                yield("0xFFFF")
                yield("0xabcdef")
                yield("0x_abc_def")
                yield("+0xFFFF")
                yield("-0xFFFF")
                yield("0x80000001")
                yield("-0x80000001")
                yield("0xFFFFFFFF")
                yield("-0xFFFFFFFF")
                yield("0o123")
                yield("-0o123")
                yield("-0o123")
                yield("+0o123")

                for (literal in arrayOf("123", "0xFFFF", "0o123", "0b01011")) {
                    for (ch in "iu") {
                        for (length in intArrayOf(8, 16, 32, 64, 128)) {
                            val suffix = "$ch$length"
                            yield("$literal$suffix")
                            yield("${literal}_$suffix")
                            yield("$literal${suffix}hello")
                            yield("+${literal}_$suffix")
                            yield("-${literal}_$suffix")
                        }
                    }
                }
            }
        )
    }

    fun testFloats() {
        doTestMultiFragments(
            sequence {
                yield("1.0")
                yield("1.0hello")
                yield("+1.0")
                yield("-1.0")
                yield("0.5")
                yield("+0.5")
                yield("-0.5")
                yield("1_234.567_890")
                yield("1234.567890")
                yield("1e23")
                yield("1e-23")
                yield("1e+23")
                yield("1.2e+23")

                for (length in intArrayOf(32, 64)) {
                    val suffix = "f$length"
                    yield("0$suffix")
                    yield("0_$suffix")
                    yield("1.0$suffix")
                    yield("+1.0$suffix")
                    yield("-1.0$suffix")
                    yield("1.0${suffix}hello")
                    yield("1_234.567_890_$suffix")
                    yield("1e+23_$suffix")
                    yield("1.2e+23_$suffix")
                }
            }
        )
    }

    fun testIdentifiers() {
        doTestMultiFragments(
            sequence {
                yield("_")

                yield("test")
                yield("Test")

                yield("$~")
                yield("$?")
                yield("$123")
                yield("\$foo")

                yield("@foo")
                yield("@@foo")

                yield("ident")
                yield("something")
                yield("with_underscores")
                yield("with_1")
                yield("foo?")
                yield("bar!")
                yield("fooBar")
                yield("❨╯°□°❩╯︵┻━┻")

                yield("def?")
                yield("if?")
                yield("else?")
                yield("elsif?")
                yield("end?")
                yield("true?")
                yield("false?")
                yield("class?")
                yield("while?")
                yield("do?")
                yield("yield?")
                yield("return?")
                yield("unless?")
                yield("next?")
                yield("break?")
                yield("begin?")

                yield("def!")
                yield("if!")
                yield("else!")
                yield("elsif!")
                yield("end!")
                yield("true!")
                yield("false!")
                yield("class!")
                yield("while!")
                yield("nil!")
                yield("do!")
                yield("yield!")
                yield("return!")
                yield("unless!")
                yield("next!")
                yield("break!")
                yield("begin!")
            }
        )
    }

    fun testKeywords() {
        CR_KEYWORDS.types.forEach {
            val kw = it.toString()
            doCustomTest(
                """
                    def foo($kw); end
                    def foo(foo $kw); end
                    def foo($kw foo); end
                    
                    foo { |$kw| }
                    foo { |($kw)| }
                """.trimIndent()
            ) { replace("\$kw", kw) }
        }
    }

    fun testKeywordsInDefParamsWithAt() {
        CR_KEYWORDS.types.forEach {
            val kw = it.toString()
            if (kw.endsWith("?")) return@forEach
            doCustomTest(
                """
                    def foo(@$kw); end
                    def foo(@@$kw); end
                """.trimIndent()
            ) { replace("\$kw", kw) }
        }
    }
    
    private val keywordsForbiddenInVarDef = TokenSet.create(
        CR_AS, 
        CR_AS_QUESTION, 
        CR_IS_A, 
        CR_IS_NIL,
        CR_OUT,
        CR_RESPONDS_TO
    )

    fun testKeywordsInVarDefinition() {
        CR_KEYWORDS.types.forEach {
            if (it in keywordsForbiddenInVarDef) return@forEach
            val kw = it.toString()
            doCustomTest(
                """
                    $kw : Int32
                    property $kw : Int32
                """.trimIndent()
            ) { replace("\$kw", kw) }
        }
    }

    fun testKeywordsForbiddenAsReferences() {
        listOf(
            CR_DEF, CR_CLASS, CR_STRUCT, CR_MODULE, CR_FUN, CR_ALIAS, CR_ABSTRACT,
            CR_INCLUDE, CR_EXTEND, CR_LIB
        ).forEach {
            val kw = it.toString()
            doTestError(
                """
                    def foo
                    $kw
                    end
                """.trimIndent()
            )
        }
    }

    fun testComboAssignments() {
        listOf("+", "-", "*", "/", "//", "%", "|", "&", "^", "**", "<<", ">>", "&+", "&-", "&*").forEach { op ->
            doCustomTest(
                """
                    f.x $op= 2
                    a = 1; a $op= 1
                    a = 1; a $op=
                    1
                    a.b $op=
                    1
                """.trimIndent()
            ) { replace("\$op", op) }
        }
    }

    fun testOperatorDefs() {
        listOf("/", "<", "<=", "==", "!=", "=~", "!~", ">", ">=", "+", "-", "*", "/", "~", "%", "&", "|", "^", "**", "===").forEach { op ->
            doCustomTest(
                """
                    def $op; end;
                    def $op(); end;
                    def self.$op; end;
                    def self.$op(); end;
                """.trimIndent()
            ) { replace("\$op", op) }
        }
    }

    fun testBinaryOperators() {
        listOf("<<", "<", "<=", "==", ">>", ">", ">=", "+", "-", "*", "/", "//", "%", "|", "&", "^", "**", "===", "=~", "!~", "&+", "&-", "&*", "&**").forEach { op ->
            doCustomTest(
                """
                    1 $op 2
                    n $op 2
                    foo(n $op 2)
                    foo(0, n $op 2)
                    foo(a: n $op 2)
                    foo(z: 0, a: n $op 2)
                    def $op(); end
                    foo = 1; ->foo.$op(Int32)
                """.trimIndent()
            ) { replace("\$op", op) }
        }
    }

    fun testQualifiedOperatorCalls() {
        listOf("+", "-", "*", "/", "<", "<=", "==", ">", ">=", "%", "|", "&", "^", "**", "===", "=~", "!~").forEach {
            doCustomTest(
                """
                    foo.$it
                    foo.$it 1, 2
                    foo.$it(1, 2)
                """.trimIndent()
            ) { replace("\$op", it) }
        }
    }

    fun testHeredocs() {
        doTestMultiFragments(
            "<<-HERE\nHello, mom! I am HERE.\nHER dress is beautiful.\nHE is OK.\n  HERESY\nHERE",
            "<<-HERE\n   One\n  Zero\n  HERE",
            "<<-HERE\n   One \\n Two\n  Zero\n  HERE",
            "<<-HERE\n   One\n\n  Zero\n  HERE",
            "<<-HERE\n   One\n \n  Zero\n  HERE",
            "<<-HERE\n   #{1}One\n  #{2}Zero\n  HERE",
            "<<-HERE\n  foo#{1}bar\n   baz\n  HERE",
            "<<-HERE\r\n   One\r\n  Zero\r\n  HERE",
            "<<-HERE\r\n   One\r\n  Zero\r\n  HERE\r\n",
            "<<-SOME\n  Sa\n  Se\n  SOME",
            "<<-HERE\n  #{1} #{2}\n  HERE",
            "<<-HERE\n  #{1} \\n #{2}\n  HERE",
            "<<-HERE\nHERE",
            "<<-HERE1; <<-HERE2\nHERE1\nHERE2",
            "<<-HERE1; <<-HERE2\nhere1\nHERE1\nHERE2",
            "<<-HERE1; <<-HERE2\nHERE1\nhere2\nHERE2",
            "<<-HERE\n   One\nwrong\n  Zero\n  HERE",
            "<<-HERE\n   One\n wrong\n  Zero\n  HERE",
            "<<-HERE\n   One\n #{1}\n  Zero\n  HERE",
            "<<-HERE\n   One\n  #{1}\n wrong\n  HERE",
            "<<-HERE\n   One\n  #{1}\n wrong#{1}\n  HERE",
            "<<-HERE\n One\n  #{1}\n  HERE",
            "\"#{<<-HERE}\"\nHERE",
            "\"foo\" \"bar\"",
            "<<-'HERE'\n  hello \\n world\n  #{1}\n  HERE",
            "<<-'HERE\n",
            "<<-'HERE COMES HEREDOC'\n  hello \\n world\n  #{1}\n  HERE COMES HEREDOC",
            "<<-EOF.x\n  foo\nEOF",
            "<<-'EOF'.x\n  foo\nEOF",
            "<<-FOO\n1\nFOO.bar",
            "<<-FOO\n1\nFOO + 2",
            "<<-FOO\n\t1\n\tFOO",
            "<<-FOO\n \t1\n \tFOO",
            "<<-FOO\n \t 1\n \t FOO",
            "<<-FOO\n\t 1\n\t FOO",
            "x, y = <<-FOO, <<-BAR\nhello\nFOO\nworld\nBAR",
            "x, y, z = <<-FOO, <<-BAR, <<-BAZ\nhello\nFOO\nworld\nBAR\n!\nBAZ",
            "<<-HEREDOC",
            "<<-HEREDOC\n"
        )
    }

    fun testStringArrayLiterals() {
        doTestMultiFragments(
            "%w{one  two}",
            "%w{one\ntwo}",
            "%w{one\ttwo}",
            "%w{\n}",
            "%w{one\\ two}",
            "%w{one{} two}",
            "%w{\\{one}",
            "%w{one\\}}",
            "%w(",
            "%w{one}}",
            "%w{{one}",
            "%i(one\\ two)",
            "%i{(one two)}",
            "%i((one two))",
            "%i(foo(bar) baz)",
            "%i{foo\\nbar baz}",
            "%i(",
            "%i{one}}",
            "%i{{one}",
        )
    }

    fun testWithRN() {
        doTestMultiFragments(
            "class Foo\r\nend\r\n\r\n1"
        )
    }
}
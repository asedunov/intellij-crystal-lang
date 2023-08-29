package org.crystal.intellij.tests.completion

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.completion.*
import org.crystal.intellij.lexer.*

private val NONE = emptyList<CrystalTokenType>()

class CrystalKeywordCompletionVariantsTest : BasePlatformTestCase() {
    private fun doTest(text: String, keywords: Collection<CrystalTokenType>) {
        myFixture.configureByText("a.cr", text)
        val actualLookups = myFixture.completeBasic().filter { it.`object` is CrystalTokenType }.map { it.lookupString }
        val expectedLookups = keywords.map { it.name }
        TestCase.assertEquals(expectedLookups, actualLookups)
    }

    private infix fun String.expects(keywords: Collection<CrystalTokenType>) {
        doTest(this, keywords)
    }

    private infix fun String.expects(keyword: CrystalTokenType) {
        doTest(this, listOf(keyword))
    }

    fun testAbstract() {
        "abstract <caret>" expects AFTER_ABSTRACT_KEYWORDS
    }

    fun testAlias() {
        "alias A = <caret>" expects ALIAS_RHS_START_KEYWORDS
        "alias <caret>" expects NONE
        "alias A <caret> =" expects NONE
        "alias A = <caret> B" expects ALIAS_RHS_START_KEYWORDS
        "alias A = B <caret>" expects NONE
    }

    fun testArgumentList() {
        "foo(<caret>)" expects ARGUMENT_START_KEYWORDS
        "foo(<caret>, 1)" expects ARGUMENT_START_KEYWORDS
        "foo(1, <caret>)" expects ARGUMENT_START_KEYWORDS
        "foo(1, <caret>, 2)" expects ARGUMENT_START_KEYWORDS
        "foo <caret>" expects ARGUMENT_START_KEYWORDS
        "foo <caret>, 1" expects ARGUMENT_START_KEYWORDS
        "foo 1, <caret>" expects ARGUMENT_START_KEYWORDS
        "foo 1, <caret>, 2" expects ARGUMENT_START_KEYWORDS
    }

    fun testArrayLiteral() {
        "[<caret>]" expects GENERAL_EXPRESSION_START_KEYWORDS
        "[<caret>, 1]" expects GENERAL_EXPRESSION_START_KEYWORDS
        "[1, <caret>]" expects GENERAL_EXPRESSION_START_KEYWORDS
        "[1, <caret>, 2]" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testAs() {
        "1.as(<caret>)" expects TYPE_START_KEYWORDS
        "1.as(Int32 <caret>)" expects NONE
        "1.as(<caret> Int32)" expects TYPE_START_KEYWORDS
        "1.as <caret>" expects TYPE_START_KEYWORDS
        "1.as Int32 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "1.as <caret> Int32" expects TYPE_START_KEYWORDS
    }

    fun testAsmOperand() {
        """asm("nop" : "a"(<caret>))""" expects GENERAL_EXPRESSION_START_KEYWORDS
        """asm("nop" : "a"(<caret> 1))""" expects GENERAL_EXPRESSION_START_KEYWORDS
        """asm("nop" : "a"(1 <caret>))""" expects EXPRESSION_SUFFIX_START_KEYWORDS
        """asm("nop" : "a"<caret>())""" expects NONE
    }

    fun testAssignment() {
        "<caret> = 1" expects CR_SELF
        "a <caret> = 1" expects CR_SELF
        "<caret> a = 1" expects TOP_LEVEL_EXPRESSION_START_KEYWORDS
        "a = <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_UNINITIALIZED)
        "a = 1 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "a = <caret> 1" expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_UNINITIALIZED)
    }

    fun testAtomicSuffix() {
        "1.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "1.5.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "false.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "true.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "'a'.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "nil.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "__DIR__.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "__END_LINE__.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "__FILE__.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "__LINE__.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        ":foo.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "`foo`.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "%x(foo).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "/foo/.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "%r(foo).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "\"foo\".<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "%(foo).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        """
            <<-HERE.<caret>
            foo
            HERE
        """.trimIndent() expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "%w(foo bar baz).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "%i(foo bar baz).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "[1, 2, 3].<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "{1, 2, 3}.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "{a: 1, b: 2, c: 3}.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "{a => 1, b => 2, c => 3}.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "->{}.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "->foo.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "asm(\"nop\" : \"a\"(1) : \"b\"(2)).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "1.as(Int).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "1.as Int.<caret>" expects CR_CLASS
        "1.as?(Int).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "1.as? Int.<caret>" expects CR_CLASS
        "1.is_a?(Int).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "1.is_a? Int.<caret>" expects CR_CLASS
        "1.nil?.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "1.nil?().<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "1.responds_to? :foo.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "1.responds_to?(:foo).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "foo.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "foo(1, 2).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "foo(1, 2) {}.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "foo 1, 2.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "foo[0].<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "instance_sizeof(Int32).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "sizeof(Int32).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "offsetof(Int32, @foo).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "typeof(foo).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "(1 + 2).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "A.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "A?.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "A(B).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "pointerof(x).<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        """
            foo 1, 2 do
            end.<caret>
        """.trimIndent() expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        """
            begin
            end.<caret>
        """.trimIndent() expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "1 + 2.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "+1.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "a!.<caret>" expects ATOMIC_METHOD_SUFFIX_START_KEYWORDS
        "@[Foo].<caret>" expects NONE
        "require \"foo\".<caret>" expects NONE
        "a = uninitialized Int32.<caret>" expects CR_CLASS
    }

    fun testBeginEnd() {
        """
            begin
                <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_RESCUE, CR_ENSURE)
        """
            begin
                A = 1
                <caret>
                B = 2
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testBinaryExpression() {
        "<caret> + 1" expects GENERAL_EXPRESSION_START_KEYWORDS
        "a <caret> + 1" expects GENERAL_EXPRESSION_START_KEYWORDS
        "<caret> a + 1" expects TOP_LEVEL_EXPRESSION_START_KEYWORDS
        "a + <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
        "a + 1 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "a + <caret> 1" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testBreak() {
        "break <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
        "break <caret>, 1" expects GENERAL_EXPRESSION_START_KEYWORDS
        "break 1, <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testCField() {
        """
            lib L
                struct S
                    x : <caret>
                end
            end
        """.trimIndent() expects TYPE_START_KEYWORDS
        """
            lib L
                struct S
                    x : <caret> T
                end
            end
        """.trimIndent() expects TYPE_START_KEYWORDS
        """
            lib L
                struct S
                    x : T <caret>
                end
            end
        """.trimIndent() expects C_STRUCT_OR_UNION_ITEM_START_KEYWORDS
    }

    fun testCFieldGroup() {
        """
            lib L
                struct S
                    x, y : <caret>
                end
            end
        """.trimIndent() expects TYPE_START_KEYWORDS
        """
            lib L
                struct S
                    x, y : <caret> T
                end
            end
        """.trimIndent() expects TYPE_START_KEYWORDS
        """
            lib L
                struct S
                    x, y : T <caret>
                end
            end
        """.trimIndent() expects C_STRUCT_OR_UNION_ITEM_START_KEYWORDS
    }

    fun testCStructBody() {
        """
            lib L
                struct S
                    <caret>
                end
            end
        """.trimIndent() expects C_STRUCT_OR_UNION_ITEM_START_KEYWORDS
        """
            lib L
                struct S
                    a : Int32
                    <caret>
                    b : Int32
                end
            end
        """.trimIndent() expects C_STRUCT_OR_UNION_ITEM_START_KEYWORDS
    }

    fun testCUnionBody() {
        """
            lib L
                union U
                    <caret>
                end
            end
        """.trimIndent() expects C_STRUCT_OR_UNION_ITEM_START_KEYWORDS
        """
            lib L
                union U
                    a : Int32
                    <caret>
                    b : Int32
                end
            end
        """.trimIndent() expects C_STRUCT_OR_UNION_ITEM_START_KEYWORDS
    }

    fun testCase() {
        """
            case <caret>
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_ELSE, CR_IN, CR_WHEN)
        """
            case <caret>
            else
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_IN, CR_WHEN)
        """
            case <caret>
            when 1
            else
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_WHEN)
        """
            case <caret>
            in 1
            else
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_IN)
        """
            case x
            when <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            case x
            when 1
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_WHEN, CR_ELSE)
        """
            case x
            when 1
              <caret>
            else  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_WHEN)
        """
            case x
            when 1
              <caret>
            when 2  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_WHEN)
        """
            case x
            when 1
            else
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            case x
            in <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            case x
            in 1
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_IN)
        """
            case x
            in 1
              <caret>
            in 2  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_IN)
    }

    fun testClassBody() {
        """
            class X
                <caret>
            end
        """.trimIndent() expects TYPE_ITEM_START_KEYWORDS
        """
            class X
                A = 1
                <caret>
                B = 2
            end
        """.trimIndent() expects TYPE_ITEM_START_KEYWORDS
    }

    fun testConditionalExpression() {
        "<caret> ? 1 : 2" expects GENERAL_EXPRESSION_START_KEYWORDS
        "x ? <caret> : 2" expects GENERAL_EXPRESSION_START_KEYWORDS
        "x ? 1 : <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testConstant() {
        "A = <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
        "A = 1 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "A = <caret> 1" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testCurlyBlock() {
        """
            foo {
                <caret>
            }
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            foo {
                A = 1
                <caret>
                B = 2
            }
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testDoBlock() {
        """
            foo do
                <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            foo do
                A = 1
                <caret>
                B = 2
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testDoubleSplatArgument() {
        "foo(**<caret>)" expects GENERAL_EXPRESSION_START_KEYWORDS
        "foo(**x <caret>)" expects ARGUMENT_START_KEYWORDS
        "foo(**<caret> x)" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testDoubleSplatTypeElement() {
        """
            def foo(**x : **<caret>)
            end
        """.trimIndent() expects TYPE_START_KEYWORDS_NO_TYPEOF
        """
            def foo(**x : **<caret> x)
            end
        """.trimIndent() expects TYPE_START_KEYWORDS_NO_TYPEOF
        """
            def foo(**x : **x <caret>)
            end
        """.trimIndent() expects NONE
    }

    fun testExceptionHandler() {
        """
            begin
            rescue <caret>
            end
        """.trimIndent() expects NONE
        """
            begin
            rescue x
              <caret>  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_ELSE, CR_ENSURE)
        """
            begin
            rescue x
              <caret>
            ensure    
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_ELSE)
        """
            begin
            rescue x
              <caret>
            else    
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_ENSURE)
        """
            begin
            rescue x
              <caret>
            else
            ensure    
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            begin
            rescue
            else
              <caret>  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_ENSURE)
        """
            begin
            rescue
            else
              <caret>
            ensure    
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            begin
            rescue
            ensure
              <caret>  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testExpressionSuffix() {
        "1 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "require \"foo\" <caret>" expects NONE
    }

    fun testExpressionTypeElement() {
        "x : typeof(<caret>)" expects GENERAL_EXPRESSION_START_KEYWORDS
        "x : typeof(1 <caret>)" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "x : typeof(<caret> 1)" expects GENERAL_EXPRESSION_START_KEYWORDS
        "x : typeof(1, <caret>)" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testFile() {
        "<caret>" expects TOP_LEVEL_EXPRESSION_START_KEYWORDS
        """
            A = 1
            <caret>
            B = 2
        """.trimIndent() expects TOP_LEVEL_EXPRESSION_START_KEYWORDS
        """
            A = 1; <caret>
            B = 2
        """.trimIndent() expects TOP_LEVEL_EXPRESSION_START_KEYWORDS
    }

    fun testFunction() {
        """
            fun foo <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_RESCUE, CR_ENSURE)
        """
            fun foo
                1
                <caret>
                2
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            lib L
                fun foo <caret>
            end
        """.trimIndent() expects LIB_ITEM_START_KEYWORDS
    }

    fun testHashLiteral() {
        "{a => <caret>}" expects GENERAL_EXPRESSION_START_KEYWORDS
        "{<caret> => b}" expects GENERAL_EXPRESSION_START_KEYWORDS
        "{a => b, <caret>}" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testHashTypeElement() {
        "{a => b} of <caret> => Int" expects TYPE_START_KEYWORDS
        "{a => b} of Int => <caret>" expects TYPE_START_KEYWORDS
    }

    fun testIf() {
        """
            if <caret>
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            if x
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_ELSE, CR_ELSIF)
        """
            if x
              foo
              <caret>
              bar
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_ELSE, CR_ELSIF)
        """
            if x
              <caret>
            else  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            if x
              <caret>
            elsif y  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            if x
              foo
            elsif <caret>  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            if x
              foo
            else
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testInstanceSizeOf() {
        "instance_sizeof(<caret>)" expects TYPE_START_KEYWORDS
        "instance_sizeof(Int32 <caret>)" expects NONE
        "instance_sizeof(<caret> Int32)" expects TYPE_START_KEYWORDS
    }

    fun testInstantiatedTypeElement() {
        "a : Foo(<caret>)" expects TYPE_ARGUMENT_START_KEYWORD
        "a : Foo(Int32, <caret>)" expects TYPE_ARGUMENT_START_KEYWORD
        "a : Foo(<caret>, Int32)" expects TYPE_ARGUMENT_START_KEYWORD
    }

    fun testIs() {
        "1.is_a?(<caret>)" expects TYPE_START_KEYWORDS
        "1.is_a?(Int32 <caret>)" expects NONE
        "1.is_a?(<caret> Int32)" expects TYPE_START_KEYWORDS
        "1.is_a? <caret>" expects TYPE_START_KEYWORDS
        "1.is_a? Int32 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "1.is_a? <caret> Int32" expects TYPE_START_KEYWORDS
    }

    fun testIsNilMacro() {
        "{{ 1.nil<caret> }}" expects NONE
        "{% 1.nil<caret> %}" expects NONE
        """
            {% if (1.nil<caret>) %}
            {% end %}
        """.trimIndent() expects NONE
        """
            {% unless (1.nil<caret>) %}
            {% end %}
        """.trimIndent() expects NONE
        """
            macro foo
                %var{1.nil<caret>, x} = hello
            end
        """.trimIndent() expects NONE
        """
            {% for x in 1.nil<caret> %}
            {% end %}
        """.trimIndent() expects NONE
    }

    fun testLabeledTypeElement() {
        "x: {a: <caret>}" expects TYPE_START_KEYWORDS
        "x: {a: <caret> Int32}" expects TYPE_START_KEYWORDS
        "x: {a: Int32 <caret>}" expects NONE
    }

    fun testLibBody() {
        """
            lib X
                <caret>
            end
        """.trimIndent() expects LIB_ITEM_START_KEYWORDS
        """
            lib X
                type A = Int
                <caret>
                type B = Int
            end
        """.trimIndent() expects LIB_ITEM_START_KEYWORDS
    }

    fun testListExpression() {
        "a, <caret> = x" expects CR_SELF
        "a, b = x, <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testMetaclass() {
        "def foo(**a : **A.<caret>); end" expects CR_CLASS
        "a : typeof(1).<caret>" expects CR_CLASS
        "{a => b} of A => B.<caret>" expects CR_CLASS
        "a : A(B, C).<caret>" expects CR_CLASS
        "a : {A.<caret>}" expects CR_CLASS
        "a : A.class.<caret>" expects CR_CLASS
        "a : {a: A, b: B, c: C}.<caret>" expects CR_CLASS
        "a : {a: A.<caret>}" expects CR_CLASS
        "a : A?.<caret>" expects CR_CLASS
        "a : (A | B | C).<caret>" expects CR_CLASS
        "a : A.<caret>" expects CR_CLASS
        "a : A*.<caret>" expects CR_CLASS
        "a : A -> B.<caret>" expects CR_CLASS
        "a : self.<caret>" expects CR_CLASS
        "a : self?.<caret>" expects CR_CLASS
        "foo(*a : *A.<caret>); end" expects CR_CLASS
        "a : A[100].<caret>" expects CR_CLASS
        "a : {A, B, C}.<caret>" expects CR_CLASS
        "a : _.<caret>" expects CR_CLASS
        "a : A | B | C.<caret>" expects CR_CLASS
    }

    fun testMethod() {
        """
            def foo <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_RESCUE, CR_ENSURE)
        """
            def foo
                1
                <caret>
                2
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testModuleBody() {
        """
            module X
                <caret>
            end
        """.trimIndent() expects TYPE_ITEM_START_KEYWORDS
        """
            module X
                A = 1
                <caret>
                B = 2
            end
        """.trimIndent() expects TYPE_ITEM_START_KEYWORDS
    }

    fun testNamedArgument() {
        "foo(x: <caret>)" expects ARGUMENT_START_KEYWORDS
        "foo(x: <caret> 1)" expects ARGUMENT_START_KEYWORDS
        "foo(x: 1 <caret>)" expects ARGUMENT_START_KEYWORDS.extend(EXPRESSION_SUFFIX_START_KEYWORDS)
    }

    fun testNamedTupleEntry() {
        "{x: <caret>}" expects GENERAL_EXPRESSION_START_KEYWORDS
        "{x: <caret> 1}" expects GENERAL_EXPRESSION_START_KEYWORDS
        "{x: 1 <caret>}" expects EXPRESSION_SUFFIX_START_KEYWORDS
    }

    fun testNext() {
        "next <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
        "next <caret>, 1" expects GENERAL_EXPRESSION_START_KEYWORDS
        "next 1, <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testOfArray() {
        "[] <caret>" expects CR_OF
        "[1, 2, 3] <caret>" expects CR_OF
        "[1, 2, 3] of <caret>" expects TYPE_START_KEYWORDS
        "[1, 2, 3] of <caret> Int32" expects TYPE_START_KEYWORDS
        "[1, 2, 3] of Int32 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS
    }

    fun testOfTuple() {
        "{1, 2, 3} <caret>" expects NONE
        "{a: 1, b: 2, c: 3} <caret>" expects NONE
    }

    fun testOfHash() {
        "{a => 1, b => 2, c => 3} <caret>" expects CR_OF
        "{a => 1, b => 2, c => 3} of <caret>" expects TYPE_START_KEYWORDS
        "{a => 1, b => 2, c => 3} of Int32 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "{a => 1, b => 2, c => 3} of <caret> Int32" expects TYPE_START_KEYWORDS
    }

    fun testOfCustomLiteral() {
        "Foo{1, 2, 3} <caret>" expects NONE
        "Foo{a => 1, b => 2, c => 3} <caret>" expects NONE
    }

    fun testOffset() {
        "offsetof(<caret>)" expects TYPE_START_KEYWORDS
        "offsetof(<caret> Int32)" expects TYPE_START_KEYWORDS
        "offsetof(Int32 <caret>)" expects NONE
    }

    fun testParenthesizedExpression() {
        "(<caret>)" expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            (1
            <caret>
            2)
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testParenthesizedTypeElement() {
        "a : (<caret>)" expects TYPE_START_KEYWORDS
    }

    fun testPointer() {
        "pointerof(<caret>)" expects GENERAL_EXPRESSION_START_KEYWORDS
        "pointerof(1 <caret>)" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "pointerof(<caret> 1)" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testPrivate() {
        "private <caret>" expects AFTER_PRIVATE_KEYWORDS
    }

    fun testProcType() {
        "a : <caret> -> Int32" expects TYPE_START_KEYWORDS
        "a : Int32, <caret> -> Int32" expects TYPE_START_KEYWORDS
        "a : <caret>, Int32 -> Int32" expects TYPE_START_KEYWORDS
        "a : Int32 -> <caret>" expects TYPE_START_KEYWORDS
        "a : Int32 -> Int32 <caret>" expects TOP_LEVEL_EXPRESSION_START_KEYWORDS
        "a : Int32 -> <caret> Int32" expects NONE
    }

    fun testProtected() {
        "protected <caret>" expects AFTER_PROTECTED_KEYWORDS
    }

    fun testReturn() {
        "return <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
        "return <caret>, 1" expects GENERAL_EXPRESSION_START_KEYWORDS
        "return 1, <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testSelect() {
        """
            select <caret>
        """.trimIndent() expects listOf(CR_ELSE, CR_WHEN)
        """
            select <caret>
            else
            end
        """.trimIndent() expects CR_WHEN
        """
            select <caret>
            when x()
            else
            end
        """.trimIndent() expects CR_WHEN
        """
            select
            when <caret>
            end
        """.trimIndent() expects CR_SELF
        """
            select
            when x()
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_WHEN, CR_ELSE)
        """
            select
            when x()
              <caret>
            else  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_WHEN)
        """
            select
            when x()
              <caret>
            when y  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_WHEN)
        """
            select
            when x()
            else
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testShortBlockArgument() {
        "foo(&<caret>)" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testSimpleParameter() {
        """
            def foo(a = <caret>)
            end
        """.trimIndent() expects PARAM_VALUE_START_KEYWORDS
        """
            def foo(a = 1 <caret>)
            end
        """.trimIndent() expects EXPRESSION_SUFFIX_START_KEYWORDS
        """
            def foo(a = <caret> 1)
            end
        """.trimIndent() expects PARAM_VALUE_START_KEYWORDS
    }

    fun testSizeOf() {
        "sizeof(<caret>)" expects TYPE_START_KEYWORDS
    }

    fun testSplatArgument() {
        "foo(*<caret>)" expects GENERAL_EXPRESSION_START_KEYWORDS
        "foo(*x <caret>)" expects ARGUMENT_START_KEYWORDS
        "foo(*<caret> x)" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testSplatTypeElement() {
        """
            def foo(*x : *<caret>)
            end
        """.trimIndent() expects TYPE_START_KEYWORDS_NO_TYPEOF
        """
            def foo(*x : *<caret> x)
            end
        """.trimIndent() expects TYPE_START_KEYWORDS_NO_TYPEOF
        """
            def foo(*x : *x <caret>)
            end
        """.trimIndent() expects NONE
    }

    fun testStaticArrayType() {
        "a : Int32[<caret>]" expects TYPE_ARGUMENT_START_KEYWORD
        "a : Int32[100 <caret>]" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "a : Int32[<caret> 100]" expects NONE
    }

    fun testTupleExpression() {
        "{<caret>}" expects GENERAL_EXPRESSION_START_KEYWORDS
        "{1, <caret>}" expects GENERAL_EXPRESSION_START_KEYWORDS
        "{<caret>, 1}" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testTupleTypeElement() {
        "a : {<caret>}" expects TYPE_START_KEYWORDS
        "a : {Int32, <caret>}" expects TYPE_START_KEYWORDS
        "a : {<caret>, Int32}" expects TYPE_START_KEYWORDS
    }

    fun testStructBody() {
        """
            struct X
                <caret>
            end
        """.trimIndent() expects TYPE_ITEM_START_KEYWORDS
        """
            struct X
                A = 1
                <caret>
                B = 2
            end
        """.trimIndent() expects TYPE_ITEM_START_KEYWORDS
    }

    fun testTypeofExpression() {
        "typeof(<caret>)" expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testUnaryExpression() {
        "+ <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
        "+ 1 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS
    }

    fun testUninitialized() {
        "a = uninitialized <caret>" expects TYPE_START_KEYWORDS
        "a = uninitialized Int32 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS
        "a = uninitialized <caret> Int32" expects TYPE_START_KEYWORDS
    }

    fun testUnionTypeElement() {
        "a : Int32 | <caret>" expects TYPE_START_KEYWORDS
        "a : <caret> | Int32" expects TYPE_START_KEYWORDS
    }

    fun testUnless() {
        """
            unless <caret>
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            unless x
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_ELSE)
        """
            unless x
              foo
              <caret>
              bar
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS.extend(CR_ELSE)
        """
            unless x
              <caret>
            else  
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            unless x
              foo
            else
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testUntil() {
        """
            until <caret>
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            until x
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            until x
              foo  
              <caret>
              bar
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testWhile() {
        """
            while <caret>
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            while x
              <caret>
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
        """
            while x
              foo  
              <caret>
              bar
            end
        """.trimIndent() expects GENERAL_EXPRESSION_START_KEYWORDS
    }

    fun testWith() {
        "with <caret>" expects GENERAL_EXPRESSION_START_KEYWORDS
        "with 1 <caret>" expects EXPRESSION_SUFFIX_START_KEYWORDS.extend(CR_YIELD)
    }

    fun testYield() {
        "yield <caret>" expects ARGUMENT_START_KEYWORDS
        "yield 1, <caret>" expects ARGUMENT_START_KEYWORDS
        "yield <caret>, 1" expects ARGUMENT_START_KEYWORDS
    }
}
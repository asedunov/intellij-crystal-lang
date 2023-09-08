package org.crystal.intellij.tests.completion

import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.lang.resolve.symbols.CrMacroSym
import org.crystal.intellij.lang.resolve.symbols.CrSym
import org.crystal.intellij.tests.util.configureMultiFileByText
import org.crystal.intellij.tests.util.setupMainFile

class CrystalReferenceCompletionVariantsTest : BasePlatformTestCase() {
    private fun doTest(text: String, variants: Collection<String>, filter: (CrSym<*>) -> Boolean = { true }) {
        myFixture.configureMultiFileByText(text)
        myFixture.setupMainFile()
        val actualLookups = myFixture
            .completeBasic()
            .filter { lookup ->
                val obj = lookup.`object`
                obj is CrSym<*> && filter(obj)
            }
            .map { lookup -> LookupElementPresentation().also { lookup.renderElement(it) }.itemText }
        TestCase.assertEquals(variants, actualLookups)
    }

    private infix fun String.expects(variants: Collection<String>) {
        doTest(this, variants)
    }

    private infix fun String.expectsMacros(variants: Collection<String>) {
        doTest(this, variants) { it is CrMacroSym }
    }

    fun testFallbackTypes() {
        "x : <caret>" expects listOf(
            "AlwaysInline",
            "Array",
            "Bool",
            "CallConvention",
            "Char",
            "Class",
            "Crystal",
            "Deprecated",
            "Enum",
            "Enumerable",
            "Exception",
            "Experimental",
            "Extern",
            "Flags",
            "Float",
            "Float32",
            "Float64",
            "GC",
            "Hash",
            "Indexable",
            "Int",
            "Int8",
            "Int16",
            "Int32",
            "Int64",
            "Int128",
            "Link",
            "Naked",
            "NamedTuple",
            "Nil",
            "NoInline",
            "NoReturn",
            "Number",
            "Object",
            "Packed",
            "Pointer",
            "Primitive",
            "Proc",
            "Raises",
            "Range",
            "Reference",
            "Regex",
            "ReturnsTwice",
            "StaticArray",
            "String",
            "Struct",
            "Symbol",
            "ThreadLocal",
            "Tuple",
            "UInt8",
            "UInt16",
            "UInt32",
            "UInt64",
            "UInt128",
            "Union",
            "Value",
            "Void"
        )
    }

    fun testGlobals() {
        val defs = """
            MyConst = 1
            
            alias MyAlias = Int
            
            annotation MyAnnotation
            end
            
            class MyClass
            end
            
            enum MyEnum
            end
            
            lib MyLib
            end
            
            module MyModule
            end
            
            struct MyStruct
            end
        """.trimIndent()
        val variants = listOf(
            "MyAlias",
            "MyAnnotation",
            "MyClass",
            "MyEnum",
            "MyLib",
            "MyModule",
            "MyStruct"
        )
        val variantsWithConsts = (variants + "MyConst").sorted()

        """
            $defs
            
            my : My<caret>
        """.trimIndent() expects variants
        """
            $defs
            
            my : ::My<caret>
        """.trimIndent() expects variants
        """
            $defs
            
            a = My<caret>
        """.trimIndent() expects variantsWithConsts
        """
            $defs
            
            a = ::My<caret>
        """.trimIndent() expects variantsWithConsts
    }

    fun testTypeScope() {
        val innerVariants = listOf(
            "MyAlias",
            "MyAnnotation",
            "MyClass",
            "MyEnum",
            "MyModule",
            "MyStruct"
        )
        val outerVariants = listOf(
            "MyOuterAlias",
            "MyOuterAnnotation",
            "MyOuterClass",
            "MyOuterLib",
            "MyOuterEnum",
            "MyOuterModule",
            "MyOuterStruct"
        )
        val defs = """
            MyConst = 1
            
            alias MyAlias = Int
            
            annotation MyAnnotation
            end
            
            class MyClass
            end
            
            enum MyEnum
            end
            
            module MyModule
            end
            
            struct MyStruct
            end
        """.trimIndent()
        val outerDefs = """
            MyOuterConst = 1
            
            alias MyOuterAlias = Int
            
            annotation MyOuterAnnotation
            end
            
            class MyOuterClass
            end
            
            enum MyOuterEnum
            end
            
            lib MyOuterLib
            end
            
            module MyOuterModule
            end
            
            struct MyOuterStruct
            end
        """.trimIndent()

        """
            $outerDefs
            
            class X
                $defs
                
                my : My<caret>
            end
        """.trimIndent() expects (innerVariants + outerVariants).sorted()
        """
            $outerDefs
            
            class X
                $defs
                
                a = My<caret>
            end
        """.trimIndent() expects (innerVariants + outerVariants + "MyConst" + "MyOuterConst").sorted()
        """
            $outerDefs
            
            class X
                $defs
            end
            
            my : X::<caret>
        """.trimIndent() expects innerVariants
        """
            $outerDefs
            
            class X
                $defs
            end
            
            a = X::<caret>
        """.trimIndent() expects (innerVariants + "MyConst").sorted()
    }

    fun testLibScope() {
        val outerVariants = listOf(
            "MyOuterAlias",
            "MyOuterAnnotation",
            "MyOuterClass",
            "MyOuterConst",
            "MyOuterLib",
            "MyOuterEnum",
            "MyOuterModule",
            "MyOuterStruct"
        )
        """
            $outerVariants
            
            lib MyLib
                alias MyAlias = Int
                
                type MyType = Int
                
                struct MyStruct
                end
                
                union MyUnion
                end
                
                enum MyEnum
                end
                
                MyConst = 1
                
                ${'$'}x : My<caret>
            end
        """.trimIndent()
    }

    fun testSuperOrdinality() {
        """
            class MyA
            end
            
            class MyB
            end
            
            class MyX < My<caret>
            end
            
            class MyC
            end
        """.trimIndent() expects listOf("MyA", "MyB")
    }

    fun testSuperNoOriginalRef() {
        """
            class MyA
            end
            
            class MyB
            end
            
            class MyX < <caret>
            end
            
            class MyC
            end
        """.trimIndent() expects listOf(
            "AlwaysInline",
            "Array",
            "Bool",
            "CallConvention",
            "Char",
            "Class",
            "Crystal",
            "Deprecated",
            "Enum",
            "Enumerable",
            "Exception",
            "Experimental",
            "Extern",
            "Flags",
            "Float",
            "Float32",
            "Float64",
            "GC",
            "Hash",
            "Indexable",
            "Int",
            "Int8",
            "Int16",
            "Int32",
            "Int64",
            "Int128",
            "Link",
            "MyA",
            "MyB",
            "Naked",
            "NamedTuple",
            "Nil",
            "NoInline",
            "NoReturn",
            "Number",
            "Object",
            "Packed",
            "Pointer",
            "Primitive",
            "Proc",
            "Raises",
            "Range",
            "Reference",
            "Regex",
            "ReturnsTwice",
            "StaticArray",
            "String",
            "Struct",
            "Symbol",
            "ThreadLocal",
            "Tuple",
            "UInt8",
            "UInt16",
            "UInt32",
            "UInt64",
            "UInt128",
            "Union",
            "Value",
            "Void"
        )
    }

    fun testSupertypes() {
        """
            module MyA
                class FooA
                end
            end
            
            module MyB
                class FooB
                end
            end
            
            class MyC
                class FooC
                end
            end
            
            class MyX < MyC
                include MyA
                
                foo : Foo<caret>
                
                include MyB
            end
        """.trimIndent() expects listOf("FooA", "FooB", "FooC")
    }

    fun testIncludeOrdinality() {
        """
            module MyA
            end
            
            module MyB
            end
            
            module MyX
                include My<caret>
            end
            
            class MyC
            end
        """.trimIndent() expects listOf("MyA", "MyB", "MyX")
    }

    fun testGenericPresentation() {
        """
            class Foooo(A)
            end
            
            class Foooooo(X, Y)
            end
            
            foo : Foo<caret>
        """.trimIndent() expects listOf("Foooo(A)", "Foooooo(X, Y)")
    }

    fun testDefinitionName() {
        """
            class Foo
            end
            
            class Fo<caret>
            end
        """.trimIndent() expects emptyList()
    }

    fun testMacros() {
        """
            macro foo_bar(a)
            end
            
            macro bar_foo(a)
            end
            
            macro bar_baz(a)
            end
            
            foo<caret>
        """.trimIndent() expectsMacros listOf("foo_bar", "bar_foo")
    }

    fun testPrivateMacros() {
        """
            # !FILE! lib.cr
            
            private macro foo_lib_private(a)
            end
            
            macro foo_lib_public(a)
            end
            
            # !FILE! main.cr
            
            require "lib"
            
            private macro foo_main_private(a)
            end
            
            macro foo_main_public(a)
            end
            
            foo<caret>
        """.trimIndent() expectsMacros listOf("foo_lib_public", "foo_main_private", "foo_main_public")
    }

    fun testMacrosNoPrefix() {
        """
            private macro foo_private(a)
            end
            
            macro foo_public(a)
            end
            
            class A
                private macro bar_private(a)
                end
            
                macro bar_public(a)
                end
            end
            
            <caret>
        """.trimIndent() expectsMacros listOf("foo_private", "foo_public")
    }

    fun testMacrosInClassBodyNoPrefix() {
        """
            private macro foo_private(a)
            end
            
            macro foo_public(a)
            end
            
            class A
                private macro bar_private(a)
                end
            
                macro bar_public(a)
                end
                
                <caret>
            end
        """.trimIndent() expectsMacros listOf("bar_private", "bar_public", "foo_private", "foo_public")
    }

    fun testMacrosInClassBody() {
        """
            private macro foo_private(a)
            end
            
            macro foo_public(a)
            end
            
            class A
                private macro bar_private(a)
                end
            
                macro bar_public(a)
                end
                
                A.bar<caret>
            end
        """.trimIndent() expectsMacros listOf("bar_private", "bar_public")
    }

    fun testMacrosWithReceiverNoPrefix() {
        """
            private macro foo_private(a)
            end
            
            macro foo_public(a)
            end
            
            class A
                private macro bar_private(a)
                end
            
                macro bar_public(a)
                end
            end
            
            A.<caret>
        """.trimIndent() expectsMacros listOf("bar_public", "foo_private", "foo_public")
    }

    fun testMacrosWithReceiver() {
        """
            private macro foo_private(a)
            end
            
            macro foo_public(a)
            end
            
            class A
                private macro bar_private(a)
                end
            
                macro bar_public(a)
                end
                
                macro bar_public2(a)
                end
            end
            
            A.bar<caret>
        """.trimIndent() expectsMacros listOf("bar_public", "bar_public2")
    }
}
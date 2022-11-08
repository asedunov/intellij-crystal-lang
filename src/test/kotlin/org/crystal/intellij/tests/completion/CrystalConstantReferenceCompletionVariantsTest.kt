package org.crystal.intellij.tests.completion

import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.tests.util.setupMainFile
import org.junit.Test

class CrystalConstantReferenceCompletionVariantsTest : BasePlatformTestCase() {
    private fun doTest(text: String, variants: Collection<String>) {
        myFixture.configureByText("a.cr", text)
        myFixture.setupMainFile()
        val actualLookups = myFixture
            .completeBasic()
            .filter { lookup -> lookup.`object` is CrConstantLikeSym<*> }
            .map { lookup -> LookupElementPresentation().also { lookup.renderElement(it) }.itemText }
        TestCase.assertEquals(variants, actualLookups)
    }

    private infix fun String.expects(variants: Collection<String>) {
        doTest(this, variants)
    }

    @Test
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

    @Test
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
            "MyConst",
            "MyEnum",
            "MyLib",
            "MyModule",
            "MyStruct"
        )

        """
            $defs
            
            my : My<caret>
        """.trimIndent() expects variants
        """
            $defs
            
            my : ::My<caret>
        """.trimIndent() expects variants
    }

    @Test
    fun testTypeScope() {
        val innerVariants = listOf(
            "MyAlias",
            "MyAnnotation",
            "MyClass",
            "MyConst",
            "MyEnum",
            "MyModule",
            "MyStruct"
        )
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
            end
            
            my : X::<caret>
        """.trimIndent() expects innerVariants
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    fun testGenericPresentation() {
        """
            class Foooo(A)
            end
            
            class Foooooo(X, Y)
            end
            
            foo : Foo<caret>
        """.trimIndent() expects listOf("Foooo(A)", "Foooooo(X, Y)")
    }
}
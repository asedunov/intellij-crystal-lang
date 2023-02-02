package org.crystal.intellij.tests.completion

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CrystalCompletionInsertHandlerTest : BasePlatformTestCase() {
    private fun doTest(beforeText: String, afterText: String) {
        myFixture.configureByText("a.cr", beforeText)
        myFixture.completeBasic()
        if (myFixture.lookup != null) myFixture.finishLookup('\n')
        myFixture.checkResult(afterText)
    }

    private infix fun String.expects(after: String) {
        doTest(this, after)
    }

    fun testSpaceRequiringKeywords() {
        "abstract<caret>" expects "abstract <caret>"
        "alias<caret>" expects "alias <caret>"
        "annotation<caret>" expects "annotation <caret>"
        "as<caret>" expects "as <caret>"
        "def<caret>" expects "def <caret>"
        "enum<caret>" expects "enum <caret>"
        "is_a<caret>" expects "is_a? <caret>"
        "macro<caret>" expects "macro <caret>"
        "module<caret>" expects "module <caret>"
        "next<caret>" expects "next <caret>"
        "[1, 2, 3] of<caret>" expects "[1, 2, 3] of <caret>"
        "private<caret>" expects "private <caret>"
        "protected<caret>" expects "protected <caret>"
        "responds_to<caret>" expects "responds_to? <caret>"
        "return<caret>" expects "return <caret>"
        "struct<caret>" expects "struct <caret>"
        "a = uninitialized<caret>" expects "a = uninitialized <caret>"
        "unless<caret>" expects "unless <caret>"
        "until<caret>" expects "until <caret>"
        "while<caret>" expects "while <caret>"
        "with<caret>" expects "with <caret>"
        "yield<caret>" expects "yield <caret>"
    }

    fun testParenRequiringKeywords() {
        "asm<caret>" expects "asm(<caret>)"
        "instance_sizeof<caret>" expects "instance_sizeof(<caret>)"
        "offsetof<caret>" expects "offsetof(<caret>)"
        "pointerof<caret>" expects "pointerof(<caret>)"
        "sizeof<caret>" expects "sizeof(<caret>)"
        "typeof<caret>" expects "typeof(<caret>)"
    }

    fun testRequire() {
        "require<caret>" expects "require \"<caret>\""
    }

    fun testNonGenericTypeReference() {
        """
            class Foooo
            end
            
            c : Foo<caret>
        """.trimIndent() expects """
            class Foooo
            end
            
            c : Foooo<caret>
        """.trimIndent()
    }

    fun testGenericTypeReference() {
        """
            class Foooo(A, B)
            end
            
            c : Foo<caret>
        """.trimIndent() expects """
            class Foooo(A, B)
            end
            
            c : Foooo(<caret>)
        """.trimIndent()
    }
}
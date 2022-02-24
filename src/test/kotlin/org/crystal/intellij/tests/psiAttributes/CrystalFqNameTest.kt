package org.crystal.intellij.tests.psiAttributes

import org.crystal.intellij.psi.CrDefinitionWithFqName
import org.junit.Test

class CrystalFqNameTest : CrystalPsiAttributeTest() {
    private fun checkFqName(text: String, value: String?) {
        checkLast<CrDefinitionWithFqName>(text, { fqName?.fullName }, value)
    }

    @Test
    fun testTopLevelPaths() {
        checkFqName(
            """
                class A
                end
            """.trimIndent(),
            "A"
        )
        checkFqName(
            """
                class ::A
                end
            """.trimIndent(),
            "A"
        )
        checkFqName(
            """
                class A::B::C
                end
            """.trimIndent(),
            "A::B::C"
        )
        checkFqName(
            """
                class ::A::B::C
                end
            """.trimIndent(),
            "A::B::C"
        )
        checkFqName(
            """
                def foo
                end
            """.trimIndent(),
            "foo"
        )
    }

    @Test
    fun testModifiers() {
        checkFqName(
            """
            private class A
            end
            """.trimIndent(),
            "A"
        )
        checkFqName(
            """
            @[Foo]
            class A
            end
            """.trimIndent(),
            "A"
        )
        checkFqName(
            """
            @[Foo]
            @[Bar]
            class A
            end
            """.trimIndent(),
            "A"
        )
        checkFqName(
            """
            @[Foo] 
            private class A
            end
            """.trimIndent(),
            "A"
        )
    }

    @Test
    fun testNestedPaths() {
        checkFqName(
            """
            class X
                class A
                end
            end
            """.trimIndent(),
            "X::A"
        )
        checkFqName(
            """
            class X
                class ::A
                end
            end
            """.trimIndent(),
            "A"
        )
        checkFqName(
            """
            class X
                class A::B::C
                end
            end
            """.trimIndent(),
            "X::A::B::C"
        )
        checkFqName(
            """
            class X
                class ::A::B::C
                end
            end
            """.trimIndent(),
            "A::B::C"
        )

        checkFqName(
            """
            class X::Y
                class A
                end
            end
            """.trimIndent(),
            "X::Y::A"
        )
        checkFqName(
            """
            class X::Y
                class ::A
                end
            end
            """.trimIndent(),
            "A"
        )
        checkFqName(
            """
            class X::Y
                class A::B::C
                end
            end
            """.trimIndent(),
            "X::Y::A::B::C"
        )
        checkFqName(
            """
            class X::Y
                class ::A::B::C
                end
            end
            """.trimIndent(),
            "A::B::C"
        )
    }

    @Test
    fun testMembers() {
        checkFqName(
            """
            def foo
            end
            """.trimIndent(),
            "foo"
        )
        checkFqName(
            """
            class X
                def foo
                end
            end
            """.trimIndent(),
            "X.foo"
        )
        checkFqName(
            """
            class ::X
                def foo
                end
            end
            """.trimIndent(),
            "X.foo"
        )
        checkFqName(
            """
            class X::Y
                def foo
                end
            end
            """.trimIndent(),
            "X::Y.foo"
        )
        checkFqName(
            """
            class ::X::Y
                def foo
                end
            end
            """.trimIndent(),
            "X::Y.foo"
        )

        checkFqName(
            """
            lib Foo
                fun bar
                end
            end    
            """.trimIndent(),
            "Foo.bar"
        )
    }
}
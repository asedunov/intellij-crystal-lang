package org.crystal.intellij.tests.psiAttributes

import org.crystal.intellij.lang.psi.CrDefinitionWithFqName

class CrystalFqNameTest : CrystalPsiAttributeTest() {
    private infix fun String.hasFqName(value: String?) {
        checkLast<CrDefinitionWithFqName>(this, { fqName?.fullName }, value)
    }

    fun testTopLevelPaths() {
        """
            class A
            end
        """.trimIndent() hasFqName "A"

        """
            class ::A
            end
        """.trimIndent() hasFqName "A"

        """
            class A::B::C
            end
        """.trimIndent() hasFqName "A::B::C"

        """
            class ::A::B::C
            end
        """.trimIndent() hasFqName "A::B::C"

        """
            def foo
            end
        """.trimIndent() hasFqName "foo"
    }

    fun testModifiers() {
        """
        private class A
        end
        """.trimIndent() hasFqName "A"

        """
        @[Foo]
        class A
        end
        """.trimIndent() hasFqName "A"

        """
        @[Foo]
        @[Bar]
        class A
        end
        """.trimIndent() hasFqName "A"

        """
        @[Foo] 
        private class A
        end
        """.trimIndent() hasFqName "A"
    }

    fun testNestedPaths() {
        """
        class X
            class A
            end
        end
        """.trimIndent() hasFqName "X::A"

        """
        class X
            class ::A
            end
        end
        """.trimIndent() hasFqName "A"

        """
        class X
            class A::B::C
            end
        end
        """.trimIndent() hasFqName "X::A::B::C"

        """
        class X
            class ::A::B::C
            end
        end
        """.trimIndent() hasFqName "A::B::C"

        """
        class X::Y
            class A
            end
        end
        """.trimIndent() hasFqName "X::Y::A"

        """
        class X::Y
            class ::A
            end
        end
        """.trimIndent() hasFqName "A"

        """
        class X::Y
            class A::B::C
            end
        end
        """.trimIndent() hasFqName "X::Y::A::B::C"

        """
        class X::Y
            class ::A::B::C
            end
        end
        """.trimIndent() hasFqName "A::B::C"
    }

    fun testMembers() {
        """
        def foo
        end
        """.trimIndent() hasFqName "foo"

        """
        class X
            def foo
            end
        end
        """.trimIndent() hasFqName "X.foo"

        """
        class ::X
            def foo
            end
        end
        """.trimIndent() hasFqName "X.foo"

        """
        class X::Y
            def foo
            end
        end
        """.trimIndent() hasFqName "X::Y.foo"

        """
        class ::X::Y
            def foo
            end
        end
        """.trimIndent() hasFqName "X::Y.foo"

        """
        lib Foo
            fun bar
            end
        end    
        """.trimIndent() hasFqName "Foo.bar"
    }
}
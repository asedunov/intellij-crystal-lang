package org.crystal.intellij.tests.psiAttributes

import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.lang.psi.allDescendants
import org.crystal.intellij.util.firstInstanceOrNull

abstract class CrystalPsiAttributeTest : BasePlatformTestCase() {
    protected inline fun <reified T : PsiElement> checkFirst(text: String, attribute: T.() -> Any?, expected: Any?) {
        myFixture.configureByText("a.cr", text)
        TestCase.assertEquals(expected, myFixture.file.allDescendants().firstInstanceOrNull<T>()!!.attribute())
    }

    protected inline fun <reified T : PsiElement> checkLast(text: String, attribute: T.() -> Any?, expected: Any?) {
        myFixture.configureByText("a.cr", text)
        TestCase.assertEquals(expected, myFixture.file.allDescendants().filter(T::class.java).last()!!.attribute())
    }
}
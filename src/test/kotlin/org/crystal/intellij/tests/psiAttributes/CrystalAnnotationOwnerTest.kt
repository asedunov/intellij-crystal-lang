package org.crystal.intellij.tests.psiAttributes

import com.intellij.psi.util.parentOfType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.lang.psi.CrAnnotationExpression
import org.crystal.intellij.tests.util.findDirective
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalAnnotationOwnerTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("psi/annotationOwner")
    }

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/psi/annotationOwner").absolutePath
    }

    @Test
    fun testOwner() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)
        val file = myFixture.file
        val expectedText = file.findDirective("# OWNER: ")!!
        val ann = file.findElementAt(myFixture.editor.caretModel.offset)!!.parentOfType<CrAnnotationExpression>()!!
        val actualText = ann.owner?.text ?: "<null>"
        TestCase.assertEquals(expectedText, actualText)
    }
}
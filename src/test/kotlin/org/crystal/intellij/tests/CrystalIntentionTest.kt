package org.crystal.intellij.tests

import com.intellij.codeInsight.intention.impl.invokeAsAction
import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.tests.util.findDirective
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalIntentionTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("intentions")
    }

    @Test
    fun testIntention() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)
        val actionText = myFixture.file.findDirective("# ACTION_TEXT:")!!
        val action = myFixture.availableIntentions.first { it.text == actionText }
        val expectedFile = File(testFile.parent, testFile.nameWithoutExtension + ".after.cr")
        val expectedText = FileUtil.loadFile(expectedFile, true)
        action.invokeAsAction(myFixture.editor, myFixture.file)
        myFixture.checkResult(expectedText)
    }
}
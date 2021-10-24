package org.crystal.intellij.tests

import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalEnterHandlerTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("enterHandler")
    }

    @Test
    fun testEnter() {
        myFixture.testDataPath = testFile.parent
        val expectedFile = File(testFile.parent, testFile.nameWithoutExtension + ".after.cr")
        val expectedText = FileUtil.loadFile(expectedFile, true)
        myFixture.configureByFile(testFile.name)
        myFixture.type('\n')
        myFixture.checkResult(expectedText)
    }
}
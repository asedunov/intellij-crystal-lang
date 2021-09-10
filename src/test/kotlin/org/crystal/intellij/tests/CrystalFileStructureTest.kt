package org.crystal.intellij.tests

import com.intellij.testFramework.FileStructureTestBase
import com.intellij.testFramework.PlatformTestUtil
import org.crystal.intellij.CrystalFileType
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalFileStructureTest(private val testFile: File) : FileStructureTestBase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("structureView")
    }

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        return PlatformTestUtil.lowercaseFirstLetter(testFile.nameWithoutExtension, lowercaseFirstLetter)
    }

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/structureView").absolutePath
    }

    override fun checkResult() {
        val expectedFilePath = File(testFile.parentFile, testFile.nameWithoutExtension + ".tree").path
        PlatformTestUtil.waitWhileBusy(myPopupFixture.tree)
        assertSameLinesWithFile(expectedFilePath, PlatformTestUtil.print(myPopupFixture.tree, true).trim { it <= ' ' })
    }

    override fun getFileExtension() = CrystalFileType.defaultExtension

    @Test
    fun testFileStructure() {
        checkTree()
    }
}
package org.crystal.intellij.tests

import com.intellij.testFramework.FileStructureTestBase
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.util.PathUtil
import org.crystal.intellij.CrystalFileType
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalFileStructureTest(private val testName: String) : FileStructureTestBase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("structureView")
    }

    override fun getTestName(lowercaseFirstLetter: Boolean) = testName

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/structureView").absolutePath
    }

    override fun checkResult() {
        val expectedFileName = myFixture.testDataPath + "/" + PathUtil.makeFileName(testName, "tree")
        PlatformTestUtil.waitWhileBusy(myPopupFixture.tree)
        assertSameLinesWithFile(expectedFileName, PlatformTestUtil.print(myPopupFixture.tree, true).trim { it <= ' ' })
    }

    override fun getFileExtension() = CrystalFileType.defaultExtension

    @Test
    fun testFileStructure() {
        checkTree()
    }
}
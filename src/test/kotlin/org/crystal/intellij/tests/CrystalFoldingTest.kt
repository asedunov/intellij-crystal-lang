package org.crystal.intellij.tests

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalFoldingTest(private val testName: String) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("folding")
    }

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/folding").absolutePath
    }

    @Test
    fun testFolding() {
        myFixture.testFolding("${myFixture.testDataPath}/$testName.cr")
    }
}
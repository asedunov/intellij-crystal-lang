package org.crystal.intellij.tests

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.config.findVersionOrLatest
import org.crystal.intellij.tests.util.findDirective
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalAnalysisTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("analysis")
    }

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/analysis").absolutePath
    }

    @Test
    fun testHighlighting() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)
        project.crystalSettings.update {
            languageVersion = findVersionOrLatest(myFixture.file.findDirective("# LANGUAGE_LEVEL: "))
        }
        myFixture.checkHighlighting(true, false, true)
    }
}
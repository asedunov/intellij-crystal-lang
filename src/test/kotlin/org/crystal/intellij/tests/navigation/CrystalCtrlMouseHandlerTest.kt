package org.crystal.intellij.tests.navigation

import com.intellij.codeInsight.navigation.CtrlMouseHandler
import com.intellij.openapi.util.text.StringUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.tests.util.findDirective
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.crystal.intellij.tests.util.hasDirective
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalCtrlMouseHandlerTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("navigation/ctrlMouseInfo")
    }

    @Suppress("UnstableApiUsage")
    @Test
    fun testTooltip() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)
        val noInfo = myFixture.file.hasDirective("# NO_INFO")
        val info = CtrlMouseHandler.getGoToDeclarationOrUsagesText(myFixture.editor)
        if (noInfo) {
            assertNull(info)
        }
        else {
            val expectedTooltip = myFixture.file.findDirective("# TOOLTIP: ")!!
            val actualTooltip = StringUtil.convertLineSeparators(StringUtil.removeHtmlTags(info!!, true))
            assertEquals(expectedTooltip, actualTooltip)
        }
    }
}
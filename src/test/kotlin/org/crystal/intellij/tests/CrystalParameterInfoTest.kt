package org.crystal.intellij.tests

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.utils.parameterInfo.MockCreateParameterInfoContext
import com.intellij.testFramework.utils.parameterInfo.MockParameterInfoUIContext
import com.intellij.testFramework.utils.parameterInfo.MockUpdateParameterInfoContext
import junit.framework.TestCase
import org.crystal.intellij.ide.parameterInfo.CrMacroCallParameterInfo
import org.crystal.intellij.ide.parameterInfo.CrMacroCallParameterInfoHandler
import org.crystal.intellij.tests.util.findDirectives
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalParameterInfoTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("parameterInfo")
    }

    @Test
    fun testParameterInfo() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)

        val editor = myFixture.editor
        val file = myFixture.file

        val handler = CrMacroCallParameterInfoHandler()
        val createContext = MockCreateParameterInfoContext(editor, file)
        val argList = handler.findElementForParameterInfo(createContext)!!
        handler.showParameterInfo(argList, createContext)
        val updateContext = MockUpdateParameterInfoContext(editor, file, createContext.itemsToShow).apply {
            parameterOwner = argList
        }
        handler.updateParameterInfo(argList, updateContext)
        val uiContext = MockParameterInfoUIContext(argList).apply {
            currentParameterIndex = updateContext.currentParameter
        }
        val actualHints = createContext.itemsToShow?.map {
            handler.updateUI(it as CrMacroCallParameterInfo, uiContext)
            val text = uiContext.text
            val from = uiContext.highlightStart
            val to = uiContext.highlightEnd
            text.substring(0, from) + "[" + text.substring(from, to) + "]" + text.substring(to)
        }
        val expectedHints = file.findDirectives("# HINT:")
        TestCase.assertEquals(expectedHints, actualHints)
    }
}
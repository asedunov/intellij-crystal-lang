package org.crystal.intellij.tests.navigation

import com.intellij.codeInsight.navigation.CtrlMouseAction
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.application.ReadConstraint
import com.intellij.openapi.application.constrainedReadAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlinx.coroutines.*
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
        val file = myFixture.file
        val editor = myFixture.editor
        val action = ActionManager.getInstance().getAction(IdeActions.ACTION_GOTO_DECLARATION) as CtrlMouseAction
        val info = runBlocking {
            constrainedReadAction(ReadConstraint.withDocumentsCommitted(project)) {
                action.getCtrlMouseData(editor, file, editor.caretModel.offset)
            }
        }
        val noInfo = file.hasDirective("# NO_INFO")
        if (noInfo) {
            assertNull(info)
        }
        else {
            val expectedTooltip = file.findDirective("# TOOLTIP: ")!!
            val actualTooltip = info!!.hintText
            assertEquals(expectedTooltip, actualTooltip)
        }
    }
}
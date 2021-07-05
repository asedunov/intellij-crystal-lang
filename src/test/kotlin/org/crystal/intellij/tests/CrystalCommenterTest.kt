package org.crystal.intellij.tests

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.testFramework.EditorTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalCommenterTest(private val testName: String) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("commenter")
    }

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/commenter").absolutePath
    }

    @Test
    fun testCommenter() {
        myFixture.configureByFile("$testName.cr")
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
        val expectedFilePath = "${myFixture.testDataPath}/$testName.after.cr"
        assertSameLinesWithFile(expectedFilePath, EditorTestUtil.getTextWithCaretsAndSelections(myFixture.editor))
    }
}
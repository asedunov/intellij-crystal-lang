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
class CrystalCommenterTest(private val testFile: File) : BasePlatformTestCase() {
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
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
        val expectedFilePath = File(testFile.parentFile, testFile.nameWithoutExtension + ".after.cr").path
        assertSameLinesWithFile(expectedFilePath, EditorTestUtil.getTextWithCaretsAndSelections(myFixture.editor))
    }
}
package org.crystal.intellij.tests

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.ExpectedHighlightingData
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalLineMarkerTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("lineMarkers")
    }

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/lineMarkers").absolutePath
    }

    @Test
    fun testHighlighting() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)
        val file = myFixture.file

        project.crystalSettings.update {
            mainFilePath = file.virtualFile.path
        }

        val project = myFixture.project
        val document = myFixture.editor.document

        val expectedData = ExpectedHighlightingData(document, false, false, false)
        expectedData.init()

        PsiDocumentManager.getInstance(project).commitAllDocuments()

        myFixture.doHighlighting()
        val markers = DaemonCodeAnalyzerImpl.getLineMarkers(document, project)
        expectedData.checkLineMarkers(file, markers, document.text)
    }
}
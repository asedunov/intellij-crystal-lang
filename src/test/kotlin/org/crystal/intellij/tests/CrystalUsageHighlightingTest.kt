package org.crystal.intellij.tests

import com.intellij.codeInsight.highlighting.HighlightUsagesHandler
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.ProperTextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.crystal.intellij.tests.util.hasDirective
import org.crystal.intellij.tests.util.setupLanguageVersion
import org.crystal.intellij.tests.util.setupMainFile
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalUsageHighlightingTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("usages/highlighting")
    }

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/usages/highlighting").absolutePath
    }

    @Test
    fun testHighlighting() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)

        myFixture.setupMainFile()

        val annotator = UsageAnnotator(myFixture.editor)
        try {
            myFixture.enableInspections(annotator)
            myFixture.checkHighlighting(true, false, false)
        } finally {
            myFixture.disableInspections(annotator)
        }
    }


    private class UsageAnnotator(private val myEditor: Editor) : LocalInspectionTool() {
        override fun getDisplayName() = "Usage annotator"

        override fun checkFile(
            file: PsiFile,
            manager: InspectionManager,
            isOnTheFly: Boolean
        ): Array<ProblemDescriptor>? {
            val problems = ArrayList<ProblemDescriptor>()
            val textLength = myEditor.document.textLength
            val highlightHandler =
                HighlightUsagesHandler.createCustomHandler<PsiElement>(myEditor, file, ProperTextRange(0, textLength))
            if (highlightHandler != null) {
                highlightHandler.computeUsages(highlightHandler.targets)
                for (usage in highlightHandler.readUsages) {
                    problems.add(manager.createProblemDescriptor(file, usage, "", ProblemHighlightType.WARNING, false))
                }
            }
            return problems.toArray(ProblemDescriptor.EMPTY_ARRAY)
        }
    }

}
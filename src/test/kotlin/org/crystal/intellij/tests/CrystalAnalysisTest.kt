package org.crystal.intellij.tests

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.tests.util.findDirective
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.jetbrains.annotations.Nls
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalAnalysisTest(private val testName: String) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("analysis")

        private fun PsiFile.getExtraAttribute() = findDirective("#EXTRA_ATTRIBUTE:")!!
    }

    private class AttributeAnnotator : LocalInspectionTool() {
        @Nls(capitalization = Nls.Capitalization.Sentence)
        override fun getDisplayName() = "PSI attribute annotator"

        override fun checkFile(
            file: PsiFile,
            manager: InspectionManager,
            isOnTheFly: Boolean
        ): Array<ProblemDescriptor> {
            val (className, getterName) = file.getExtraAttribute().split(".")
            @Suppress("UNCHECKED_CAST")
            val jClass = Class.forName("org.crystal.intellij.psi.$className") as Class<PsiElement>
            val getter = jClass.getMethod(getterName)
            val problems = ArrayList<ProblemDescriptor>()
            for (e in PsiTreeUtil.findChildrenOfType(file, jClass)) {
                val desc = getter(e)?.toString() ?: ""
                problems += manager.createProblemDescriptor(e, e, desc, ProblemHighlightType.WARNING, isOnTheFly)
            }
            return problems.toTypedArray()
        }
    }

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/analysis").absolutePath
    }

    @Test
    fun testHighlighting() {
        myFixture.configureByFile("$testName.cr")
        var annotator: AttributeAnnotator? = null
        try {
            if (myFixture.file.findDirective("#EXTRA_ATTRIBUTE:") != null) {
                annotator = AttributeAnnotator()
                myFixture.enableInspections(annotator)
            }
            myFixture.checkHighlighting(true, false, true)
        } finally {
            if (annotator != null) myFixture.disableInspections(annotator)
        }
    }
}
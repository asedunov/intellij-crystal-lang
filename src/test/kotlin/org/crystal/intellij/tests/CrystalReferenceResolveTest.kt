package org.crystal.intellij.tests

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.model.presentation.SymbolPresentationService
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.containers.ObjectIntHashMap
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.psi.CrVisitor
import org.crystal.intellij.resolve.symbols.CrSym
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalReferenceResolveTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("resolve/references")
    }

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/resolve/references").absolutePath
    }

    @Test
    fun testHighlighting() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)

        val file = myFixture.file
        project.crystalSettings.update {
            mainFilePath = file.virtualFile.path
        }

        val refAnnotator = RefAnnotator()
        try {
            myFixture.enableInspections(refAnnotator)
            myFixture.checkHighlighting(true, false, false)
        } finally {
            myFixture.disableInspections(refAnnotator)
        }
    }

    @Suppress("UnstableApiUsage")
    private class RefAnnotator : LocalInspectionTool() {
        override fun getDisplayName() = "Reference annotator"

        override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean) = object : CrVisitor() {
            private var nextId = 1
            private val ids = ObjectIntHashMap<CrSym<*>>()
            private val presenter = SymbolPresentationService.getInstance()

            private fun getId(sym: CrSym<*>): Int {
                if (ids.containsKey(sym)) return ids[sym]
                val id = nextId++
                ids.put(sym, id)
                return id
            }

            private fun report(e: PsiElement, message: String) {
                holder.registerProblem(e, message, ProblemHighlightType.WARNING)
            }

            override fun visitPathNameElement(o: CrPathNameElement) {
                super.visitPathNameElement(o)

                val nameElement = o.item ?: return
                val sym = o.resolveSymbol()
                if (sym != null) {
                    val presentation = presenter.getSymbolPresentation(sym)
                    val message = buildString {
                        append('#').append(getId(sym)).append(": ")
                        append(presentation.shortDescription)
                    }
                    report(nameElement, message)
                }
                else {
                    report(nameElement, "unresolved")
                }
            }
        }
    }
}
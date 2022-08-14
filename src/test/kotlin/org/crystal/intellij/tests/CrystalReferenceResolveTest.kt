package org.crystal.intellij.tests

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.model.presentation.SymbolPresentationService
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.containers.ObjectIntHashMap
import org.crystal.intellij.psi.CrNameElement
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.psi.CrSimpleNameElement
import org.crystal.intellij.psi.CrVisitor
import org.crystal.intellij.resolve.symbols.CrSym
import org.crystal.intellij.resolve.symbols.CrTypeSym
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.crystal.intellij.tests.util.hasDirective
import org.crystal.intellij.tests.util.setupMainFile
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

        myFixture.setupMainFile()

        val refAnnotator = RefAnnotator(myFixture.file.hasDirective("# WITH_METACLASS"))
        try {
            myFixture.enableInspections(refAnnotator)
            myFixture.checkHighlighting(true, false, false)
        } finally {
            myFixture.disableInspections(refAnnotator)
        }
    }

    @Suppress("UnstableApiUsage")
    private class RefAnnotator(
        private val withMetaclass: Boolean = false
    ) : LocalInspectionTool() {
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

            private fun StringBuilder.appendSym(sym: CrSym<*>): StringBuilder {
                val presentation = presenter.getSymbolPresentation(sym)
                append('#').append(getId(sym)).append(": ")
                append(presentation.shortDescription)
                return this
            }

            private fun report(e: PsiElement, message: String) {
                holder.registerProblem(e, message, ProblemHighlightType.WARNING)
            }

            override fun visitNameElement(o: CrNameElement) {
                super.visitNameElement(o)

                val nameElement = when (o) {
                    is CrPathNameElement -> o.item ?: return
                    is CrSimpleNameElement -> o
                }
                val sym = o.resolveSymbol()
                if (sym != null) {
                    val message = buildString {
                        appendSym(sym)
                        if (sym is CrTypeSym<*> && withMetaclass) {
                            append(" / ").appendSym(sym.metaclass)
                        }
                    }
                    report(nameElement, message)
                }
                else if (o is CrPathNameElement) {
                    report(nameElement, "unresolved")
                }
            }
        }
    }
}
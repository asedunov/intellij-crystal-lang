package org.crystal.intellij.tests

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.crystal.intellij.ide.presentation.shortDescription
import org.crystal.intellij.lang.psi.*
import org.crystal.intellij.lang.resolve.scopes.CrMacroParameterMatch
import org.crystal.intellij.lang.resolve.scopes.CrResolvedMacroCall
import org.crystal.intellij.lang.resolve.symbols.CrSym
import org.crystal.intellij.lang.resolve.symbols.CrTypeSym
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

    private class RefAnnotator(
        private val withMetaclass: Boolean
    ) : LocalInspectionTool() {
        override fun getDisplayName() = "Reference annotator"

        override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean) = object : CrVisitor() {
            val descriptions = buildReferenceDescriptions(withMetaclass)

            private fun buildReferenceDescriptions(withMetaclass: Boolean): List<Pair<PsiElement, String>> {
                val result = ArrayList<Pair<PsiElement, String>>()
                val visitor = object : CrRecursiveVisitor() {
                    private var nextId = 1
                    private val ids = Object2IntOpenHashMap<CrSym<*>>()

                    private fun getId(sym: CrSym<*>): Int {
                        if (ids.containsKey(sym)) return ids.getInt(sym)
                        val id = nextId++
                        ids.put(sym, id)
                        return id
                    }

                    private fun StringBuilder.appendSym(sym: CrSym<*>): StringBuilder {
                        append('#').append(getId(sym)).append(": ")
                        append(sym.shortDescription)
                        return this
                    }

                    private fun StringBuilder.appendCall(call: CrResolvedMacroCall): StringBuilder {
                        append("call<")
                        append("args: ")
                        val arguments = call.call.expression.argumentList?.elements ?: emptyList()
                        val parameters = call.macro.parameters
                        arguments.joinTo(this) {
                            parameters.indexOf(call.getArgumentMatch(it)).toString()
                        }
                        append(", params: ")
                        parameters.joinTo(this) { param ->
                            when (val match = call.getParameterMatch(param)) {
                                CrMacroParameterMatch.DefaultValue -> "<default>"
                                is CrMacroParameterMatch.Simple -> arguments.indexOf(match.argument).toString()
                                is CrMacroParameterMatch.Splat -> match.arguments.joinToString(prefix = "(", postfix = ")") {
                                    arguments.indexOf(it).toString()
                                }
                                else -> "???"
                            }
                        }
                        append(">")
                        return this
                    }

                    private fun report(e: PsiElement, message: String) {
                        result.add(e to message)
                    }

                    override fun visitNameElement(o: CrNameElement) {
                        super.visitNameElement(o)

                        val nameElement = when (o) {
                            is CrPathNameElement -> o.item ?: return
                            is CrSimpleNameElement -> o
                        }
                        val symbols = o.resolveCandidates()
                        if (symbols.isNotEmpty()) {
                            val resolvedCalls = (o.parent as? CrCallLikeExpression)?.resolveCandidateCalls() ?: emptyList()
                            val message = buildString {
                                for ((i, sym) in symbols.withIndex()) {
                                    if (i > 0) {
                                        append(", ")
                                    }
                                    appendSym(sym)
                                    if (sym is CrTypeSym<*> && withMetaclass) {
                                        append(" / ").appendSym(sym.metaclass)
                                    }
                                }
                                for (resolvedCall in resolvedCalls) {
                                    append(", ")
                                    appendCall(resolvedCall)
                                }
                            }
                            report(nameElement, message)
                        }
                        else if (o is CrPathNameElement) {
                            report(nameElement, "unresolved")
                        }
                    }
                }
                holder.file.accept(visitor)
                return result
            }

            override fun visitCrFile(o: CrFile) {
                for ((e, message) in descriptions) {
                    holder.registerProblem(e, message, ProblemHighlightType.WARNING)
                }
            }
        }
    }
}
package org.crystal.intellij.tests

import com.intellij.openapi.editor.Caret
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope.FilesScope
import com.intellij.refactoring.suggested.startOffset
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.lang.stubs.indexes.CrystalStringStubIndexExtensionBase
import org.crystal.intellij.tests.util.findDirective
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import kotlin.math.max
import kotlin.reflect.full.companionObjectInstance

@RunWith(Parameterized::class)
class CrystalIndexTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("indexes")
    }

    @Test
    fun testIndex() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)

        val file = myFixture.file
        val indexClassName = file.findDirective("# INDEX:")!!
        val key = file.findDirective("# KEY:")!!
        val indexClass = Class.forName("org.crystal.intellij.lang.stubs.indexes.$indexClassName").kotlin
        val index = indexClass.companionObjectInstance as CrystalStringStubIndexExtensionBase.HelperBase<*>
        val results = index[key, project, FilesScope.fileScope(file)].sortedBy { it.startOffset }
        val caretModel = myFixture.editor.caretModel
        val carets = caretModel.allCarets
        for (i in 0 until max(carets.size, results.size)) {
            val caret = carets.getOrNull(i)
            val result = results.getOrNull(i)
            if (caret == null) throw AssertionError("Extra result at ${result!!.formatPosition()}")
            if (result == null) throw AssertionError("Missing result at ${caret.formatPosition()}")
            if (result.startOffset != caret.offset) {
                throw AssertionError("Expected result at ${caret.formatPosition()}, found at ${result.formatPosition()}")
            }
        }
    }

    private fun Caret.formatPosition() = formatPosition(offset)

    private fun PsiElement.formatPosition() = formatPosition(startOffset)

    private fun formatPosition(offset: Int): String {
        val document = myFixture.editor.document
        val line = document.getLineNumber(offset)
        val col = offset - document.getLineStartOffset(line)
        return "${line + 1}:${col + 1}"
    }
}
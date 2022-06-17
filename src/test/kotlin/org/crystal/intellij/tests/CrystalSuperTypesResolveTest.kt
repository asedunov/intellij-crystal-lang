package org.crystal.intellij.tests

import com.intellij.psi.util.parentOfType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.psi.CrConstantSource
import org.crystal.intellij.resolve.scopes.asSequence
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.tests.util.findDirective
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.crystal.intellij.tests.util.setupMainFile
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalSuperTypesResolveTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("resolve/superClass")
    }

    override fun setUp() {
        super.setUp()
        myFixture.testDataPath = File("src/testData/resolve/superClass").absolutePath
    }

    @Test
    fun testHighlighting() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)

        myFixture.setupMainFile()

        val file = myFixture.file

        val expectedSuperClass = file.findDirective("# SUPER_CLASS:")!!
        val expectedParents = file.findDirective("# PARENTS:") ?: expectedSuperClass

        myFixture.editor.caretModel.runForEachCaret { caret ->
            val offset = caret.offset
            val typeSource = file.findElementAt(offset)!!.parentOfType<CrConstantSource>()!!
            val symbol = typeSource.resolveSymbol() as CrModuleLikeSym
            val superClass = symbol.superClass
            val parents = symbol.parents
            TestCase.assertEquals(expectedSuperClass, superClass?.fqName?.fullName ?: "")
            TestCase.assertEquals(
                expectedParents,
                parents?.asSequence()?.joinToString { it.fqName!!.fullName } ?: ""
            )
        }
    }
}
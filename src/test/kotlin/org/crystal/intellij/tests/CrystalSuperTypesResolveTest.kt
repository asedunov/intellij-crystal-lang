package org.crystal.intellij.tests

import com.intellij.psi.util.parentOfType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.lang.psi.CrConstantSource
import org.crystal.intellij.lang.resolve.scopes.asSequence
import org.crystal.intellij.lang.resolve.symbols.CrModuleLikeSym
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

        val expectedSuperClass = file.findDirective("# SUPER_CLASS:")
        val expectedMetaSuperClass = file.findDirective("# META_SUPER_CLASS:")
        val expectedParents = file.findDirective("# PARENTS:") ?: expectedSuperClass
        val expectedMetaParents = file.findDirective("# META_PARENTS:") ?: expectedMetaSuperClass

        myFixture.editor.caretModel.runForEachCaret { caret ->
            val offset = caret.offset
            val typeSource = file.findElementAt(offset)!!.parentOfType<CrConstantSource>()!!
            val symbol = typeSource.resolveSymbol() as CrModuleLikeSym
            if (expectedSuperClass != null) {
                TestCase.assertEquals(expectedSuperClass, symbol.superClass?.fqName?.fullName ?: "")
            }
            if (expectedParents != null) {
                TestCase.assertEquals(
                    expectedParents,
                    symbol.parents?.asSequence()?.joinToString { it.fqName!!.fullName } ?: ""
                )
            }
            if (expectedMetaSuperClass != null) {
                TestCase.assertEquals(expectedMetaSuperClass, symbol.metaclass.superClass?.fqName?.fullName ?: "")
            }
            if (expectedMetaParents != null) {
                TestCase.assertEquals(
                    expectedMetaParents,
                    symbol.metaclass.parents?.asSequence()?.joinToString { it.fqName!!.fullName } ?: ""
                )
            }
        }
    }
}
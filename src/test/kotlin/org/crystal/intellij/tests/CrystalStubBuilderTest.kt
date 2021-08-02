package org.crystal.intellij.tests

import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.impl.DebugUtil
import com.intellij.testFramework.LightPlatformTestCase
import com.intellij.testFramework.assertEqualsToFile
import org.crystal.intellij.psi.CrFile
import org.crystal.intellij.stubs.CrStubBuilder
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalStubBuilderTest(private val testFile: File) : LightPlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("stubs")
    }

    @Test
    fun testStubs() {
        val source = FileUtil.loadFile(testFile)
        val builder = CrStubBuilder()

        val file = createLightFile("test.cr", source) as CrFile
        val stubTree = builder.buildStubTree(file)

        val expectedFile = File(testFile.parent, testFile.nameWithoutExtension + ".txt")

        assertEqualsToFile("Stub tree differs", expectedFile, DebugUtil.stubTreeToString(stubTree))
    }
}
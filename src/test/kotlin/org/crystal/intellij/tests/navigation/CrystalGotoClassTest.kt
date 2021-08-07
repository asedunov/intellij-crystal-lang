package org.crystal.intellij.tests.navigation

import com.intellij.ide.util.gotoByName.GotoClassModel2
import com.intellij.openapi.project.Project
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalGotoClassTest(testFile: File) : CrystalGotoDeclarationTest(testFile) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("navigation/gotoDeclaration") +
                getCrystalTestFilesAsParameters("navigation/gotoClass")
    }

    override fun createModel(project: Project) = GotoClassModel2(project)
}
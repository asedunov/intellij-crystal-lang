package org.crystal.intellij.tests.navigation

import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.lang.psi.CrDefinitionWithFqName
import org.crystal.intellij.lang.psi.presentableKind
import org.crystal.intellij.tests.util.findDirective
import org.crystal.intellij.tests.util.findDirectives
import org.junit.Test
import java.io.File

abstract class CrystalGotoDeclarationTest(private val testFile: File) : BasePlatformTestCase() {
    protected abstract fun createModel(project: Project): FilteringGotoByModel<LanguageRef>

    private fun CrDefinitionWithFqName.render() = buildString {
        append(presentableKind)
        append(' ')
        append(nameElement!!.sourceName)
        fqName!!.parent?.fullName?.let {
            append(" in $it")
        }
    }

    @Suppress("JUnitMixedFramework")
    @Test
    fun testGoto() {
        myFixture.testDataPath = testFile.parent
        myFixture.configureByFile(testFile.name)
        val model = createModel(project)
        val searchText = myFixture.file.findDirective("# SEARCH_TEXT: ")!!
        val actual = model
            .getNames(true)
            .filter { it?.startsWith(searchText, true) ?: false }
            .flatMap { model.getElementsByName(it, true, "$it*").toList() }
            .map { (it as CrDefinitionWithFqName).render() }
            .sorted()
        val expected = myFixture.file.findDirectives("# RESULT: ")
        TestCase.assertEquals(expected, actual)
    }
}
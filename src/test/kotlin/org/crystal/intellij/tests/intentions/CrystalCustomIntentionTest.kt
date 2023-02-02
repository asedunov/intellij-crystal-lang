package org.crystal.intellij.tests.intentions

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.config.LanguageLevel
import org.crystal.intellij.config.asSpecificVersion
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.tests.util.withLanguageLevel

class CrystalCustomIntentionTest : BasePlatformTestCase() {
    private fun doTest(text: String, actionFamily: String, check: () -> Unit) {
        myFixture.configureByText("a.cr", text)
        val action = myFixture.availableIntentions.first { it.familyName == actionFamily }
        action.invoke(project, myFixture.editor, myFixture.file)
        check()
    }

    private fun doTestChangeLanguageVersion(
        text: String,
        initialLevel: LanguageLevel,
        targetLevel: LanguageLevel
    ) = project.withLanguageLevel(initialLevel) {
        doTest(text, CrystalBundle.message("intention.change.language.level.family")) {
            TestCase.assertEquals(targetLevel.asSpecificVersion(), project.crystalSettings.languageVersion)
        }
    }

    fun testSplatInAssignmentLHS() = doTestChangeLanguageVersion(
        "<caret>*a = 1",
        LanguageLevel.CRYSTAL_1_2,
        LanguageLevel.CRYSTAL_1_3
    )

    fun testSplatInMultiAssignmentLHS() = doTestChangeLanguageVersion(
        "<caret>*a, b = 1",
        LanguageLevel.CRYSTAL_1_2,
        LanguageLevel.CRYSTAL_1_3
    )

    fun testGlobalMatchIndexUpgrade() = doTestChangeLanguageVersion(
        "<caret>$10?",
        LanguageLevel.CRYSTAL_1_6,
        LanguageLevel.CRYSTAL_1_7
    )

    fun testAsmOptions12() = doTestChangeLanguageVersion(
        "asm(\"bl trap\" :::: <caret>\"unwind\")",
        LanguageLevel.CRYSTAL_1_1,
        LanguageLevel.CRYSTAL_1_2,
    )
}
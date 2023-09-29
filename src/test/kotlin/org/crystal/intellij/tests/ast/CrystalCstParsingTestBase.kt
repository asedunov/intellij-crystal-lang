package org.crystal.intellij.tests.ast

import com.intellij.testFramework.ParsingTestCase
import org.crystal.intellij.lang.ast.cstNode
import org.crystal.intellij.lang.ast.nodes.CstNode
import org.crystal.intellij.lang.config.CrystalProjectSettings
import org.crystal.intellij.lang.parser.CrystalParserDefinition
import org.crystal.intellij.lang.psi.CrFile
import org.crystal.intellij.lang.resolve.CrResolveFacade

abstract class CrystalCstParsingTestBase : ParsingTestCase("parser", "cr", CrystalParserDefinition()) {
    override fun getTestDataPath() = ""

    override fun setUp() {
        super.setUp()

        project.registerService(CrystalProjectSettings::class.java)
        project.registerService(CrResolveFacade::class.java)
    }

    protected fun convert(source: String): CstNode {
        myFile = createPsiFile("a", source)
        ensureParsed(myFile)
        return (myFile as CrFile).cstNode!!
    }
}
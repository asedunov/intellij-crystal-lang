package org.crystal.intellij.tests.ast

import com.intellij.testFramework.ParsingTestCase
import junit.framework.TestCase
import org.crystal.intellij.lang.ast.cstNode
import org.crystal.intellij.lang.ast.nodes.CstExpressions
import org.crystal.intellij.lang.ast.nodes.CstNode
import org.crystal.intellij.lang.ast.render
import org.crystal.intellij.lang.config.CrystalProjectSettings
import org.crystal.intellij.lang.parser.CrystalParserDefinition
import org.crystal.intellij.lang.psi.CrFile
import org.crystal.intellij.lang.resolve.CrResolveFacade
import org.crystal.intellij.util.cast

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

    protected inline fun <reified T> assertNode(source: String, body: T.(String) -> Unit) {
        convert(source).cast<T>().body(source)
    }

    protected fun assertExpressions(source: String, body: CstExpressions.(String) -> Unit) {
        assertNode(source, body)
    }

    protected fun CstNode.assertRender(expected: String) {
        TestCase.assertEquals(expected, render())
    }

    protected fun CstNode.assertRenderTrimmed(expected: String) {
        TestCase.assertEquals(expected, render().trim())
    }

    protected fun CstNode.assertNameStart(line: Int, col: Int) {
        val loc = nameLocation!!
        TestCase.assertEquals(line, loc.startLine)
        TestCase.assertEquals(col, loc.startColumn)
    }
}
package org.crystal.intellij.lang.parser

import com.intellij.lang.ASTFactory
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.crystal.intellij.lang.config.CrystalLevel
import org.crystal.intellij.lang.config.crystalSettings
import org.crystal.intellij.lang.lexer.CR_COMMENTS
import org.crystal.intellij.lang.lexer.CrystalLexer
import org.crystal.intellij.lang.lexer.CrystalTokenType
import org.crystal.intellij.lang.psi.CrFile
import org.crystal.intellij.lang.stubs.elementTypes.CrStubElementType
import org.crystal.intellij.lang.stubs.elementTypes.CrStubElementTypes

class CrystalParserDefinition : ParserDefinition, ASTFactory() {
    override fun getWhitespaceTokens() = TokenSet.EMPTY!!

    override fun getCommentTokens() = CR_COMMENTS

    override fun getStringLiteralElements() = TokenSet.EMPTY!!

    override fun getFileNodeType() = CrStubElementTypes.FILE

    override fun createLexer(project: Project?): CrystalLexer {
        val languageLevel = project?.crystalSettings?.languageVersion?.level ?: CrystalLevel.LATEST_STABLE
        return CrystalLexer(languageLevel)
    }

    override fun createParser(project: Project?): CrystalParser {
        val languageLevel = project?.crystalSettings?.languageVersion?.level ?: CrystalLevel.LATEST_STABLE
        return CrystalParser(languageLevel)
    }

    override fun createLeaf(type: IElementType, text: CharSequence) = when (type) {
        is CrystalTokenType -> type.createLeaf(text)
        else -> super.createLeaf(type, text)
    }

    override fun createElement(node: ASTNode) = when (val type = node.elementType) {
        is CrCompositeElementType -> type.factory(node)
        is CrStubElementType<*, *> -> type.astPsiFactory(node)
        else -> throw AssertionError("Unknown element type: $type")
    }

    override fun createFile(viewProvider: FileViewProvider) = CrFile(viewProvider)
}
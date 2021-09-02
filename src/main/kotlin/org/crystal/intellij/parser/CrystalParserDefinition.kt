package org.crystal.intellij.parser

import com.intellij.lang.ASTFactory
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.crystal.intellij.config.LanguageLevel
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.lexer.CR_COMMENTS
import org.crystal.intellij.lexer.CrystalLexer
import org.crystal.intellij.lexer.CrystalTokenTypeWithFactory
import org.crystal.intellij.psi.CrFile
import org.crystal.intellij.psi.CrFileElementType

class CrystalParserDefinition : ParserDefinition, ASTFactory() {
    override fun getWhitespaceTokens() = TokenSet.EMPTY!!

    override fun getCommentTokens() = CR_COMMENTS

    override fun getStringLiteralElements() = TokenSet.EMPTY!!

    override fun getFileNodeType() = CrFileElementType

    override fun createLexer(project: Project?): CrystalLexer {
        val languageLevel = project?.crystalSettings?.languageLevel ?: LanguageLevel.LATEST_STABLE
        return CrystalLexer(languageLevel)
    }

    override fun createParser(project: Project?) = CrystalParser()

    override fun createLeaf(type: IElementType, text: CharSequence) = when (type) {
        is CrystalTokenTypeWithFactory -> type.factory(type, text)
        else -> super.createLeaf(type, text)
    }

    override fun createElement(node: ASTNode): PsiElement {
        val type = node.elementType
        if (type !is CrCompositeElementType) throw AssertionError("Unknown element type: $type")
        return type.factory(node)
    }

    override fun createFile(viewProvider: FileViewProvider) = CrFile(viewProvider)
}
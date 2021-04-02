package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import org.crystal.intellij.CrystalLanguage
import org.crystal.intellij.lexer.CR_WHITESPACES_AND_NEWLINES
import org.crystal.intellij.parser.builder.LazyPsiBuilder

object CrFileElementType : IFileElementType(CrystalLanguage) {
    override fun doParseContents(chameleon: ASTNode, psi: PsiElement): ASTNode {
        val project = psi.project
        val parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(CrystalLanguage)
        val lexer = parserDefinition.createLexer(project)
        val parser = parserDefinition.createParser(project)
        val builder = object : LazyPsiBuilder(project, parserDefinition, lexer, chameleon, chameleon.chars) {
            override fun isWhitespaceOrCommentForBalance(type: IElementType?): Boolean {
                return type in CR_WHITESPACES_AND_NEWLINES || super.isWhitespaceOrCommentForBalance(type)
            }
        }
        val node = parser.parse(this, builder)
        return node.firstChildNode
    }
}
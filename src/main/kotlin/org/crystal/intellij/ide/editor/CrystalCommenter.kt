package org.crystal.intellij.ide.editor

import com.intellij.codeInsight.generation.IndentedCommenter
import com.intellij.lang.CodeDocumentationAwareCommenter
import com.intellij.psi.PsiComment
import com.intellij.psi.tree.IElementType
import org.crystal.intellij.lang.lexer.CR_LINE_COMMENT

class CrystalCommenter : CodeDocumentationAwareCommenter, IndentedCommenter {
    override fun getLineCommentPrefix(): String = "# "

    override fun getBlockCommentPrefix(): String? = null

    override fun getBlockCommentSuffix(): String? = null

    override fun getCommentedBlockCommentPrefix(): String? = null

    override fun getCommentedBlockCommentSuffix(): String? = null

    override fun getLineCommentTokenType(): IElementType = CR_LINE_COMMENT

    override fun getBlockCommentTokenType(): IElementType? = null

    override fun getDocumentationCommentTokenType(): IElementType? = null

    override fun getDocumentationCommentPrefix(): String? = null

    override fun getDocumentationCommentLinePrefix(): String? = null

    override fun getDocumentationCommentSuffix(): String? = null

    override fun isDocumentationComment(element: PsiComment): Boolean = false

    override fun forceIndentedLineComment(): Boolean = true
}
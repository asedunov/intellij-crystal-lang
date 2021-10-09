package org.crystal.intellij.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import com.intellij.refactoring.suggested.startOffset
import org.crystal.intellij.lexer.*
import org.crystal.intellij.psi.*

class CrystalFoldingBuilder : CustomFoldingBuilder() {
    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement,
        document: Document,
        quick: Boolean
    ) {
        if (root !is CrFile) return
        root
            .allDescendants()
            .filter { e ->
                (e is CrBody || e is CrThenClause || e is CrElseClause || e is CrBlockExpression) &&
                        !e.isOneLine(document)
            }
            .forEach { e -> descriptors += FoldingDescriptor(e.node, e.foldingRange) }
    }

    private fun PsiElement.isOneLine(document: Document): Boolean {
        val range = textRange
        return document.getLineNumber(range.startOffset) == document.getLineNumber(range.endOffset)
    }

    private val PsiElement.foldingRange: TextRange
        get() {
            val range = textRange

            if (firstChild.elementType in blockStartTokens) {
                var start = range.startOffset
                var end = range.endOffset
                start += firstChild.textLength
                if (lastChild.elementType in blockEndTokens) end -= lastChild.textLength
                return TextRange(start, end)
            }

            if (this is CrElseClause) return TextRange(keyword.textRange.endOffset, range.endOffset)

            val to = if (this is CrBody || this is CrBlockExpression) parent.lastChild else this
            val from = siblings(forward = false, withSelf = false).dropWhile {
                it is PsiWhiteSpace || it is PsiComment || it.elementType == CR_SEMICOLON
            }.firstOrNull()
            return TextRange(
                from?.textRange?.endOffset ?: range.startOffset,
                if (to !== this) to.startOffset else range.endOffset
            )
        }

    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        val psi = node.psi
        return when {
            psi is CrElseClause || psi is CrThenClause -> " ..."
            psi is CrBlockExpression && psi.firstChild.elementType in blockStartTokens -> " ... "
            else -> if (psi.parent.lastChild == psi) " ..." else " ... "
        }
    }

    override fun isRegionCollapsedByDefault(node: ASTNode) = false
}

private val blockStartTokens = TokenSet.create(CR_BEGIN, CR_DO, CR_LBRACE)
private val blockEndTokens = TokenSet.create(CR_END, CR_RBRACE)
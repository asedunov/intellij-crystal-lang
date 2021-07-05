package org.crystal.intellij.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
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
            if (this is CrBlockExpression) {
                var start = range.startOffset
                var end = range.endOffset
                val firstChild = firstChild
                val lastChild = lastChild
                if (firstChild.elementType in blockStartTokens) start += firstChild.textLength
                if (lastChild.elementType in blockEndTokens) end -= lastChild.textLength
                return TextRange(start, end)
            }
            return range
        }

    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange) = "..."

    override fun isRegionCollapsedByDefault(node: ASTNode) = false
}

private val blockStartTokens = TokenSet.create(CR_BEGIN, CR_DO, CR_LBRACE)
private val blockEndTokens = TokenSet.create(CR_END, CR_RBRACE)
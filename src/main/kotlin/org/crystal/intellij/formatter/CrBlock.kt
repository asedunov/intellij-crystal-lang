package org.crystal.intellij.formatter

import com.intellij.formatting.Block
import com.intellij.formatting.ChildAttributes
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import org.crystal.intellij.CrystalLanguage
import org.crystal.intellij.lexer.*
import org.crystal.intellij.psi.*

class CrBlock(
    node: ASTNode,
    private val settings: CodeStyleSettings
) : AbstractBlock(node, null, null) {
    override fun getSpacing(child1: Block?, child2: Block): Spacing? = null

    override fun isLeaf() = myNode.firstChildNode == null

    override fun buildChildren(): MutableList<Block> {
        val childBlocks = ArrayList<Block>()

        val childNodes = myNode.getChildren(null)
        if (childNodes.isEmpty()) return childBlocks

        for (childNode in childNodes) {
            if (childNode.psi !is PsiWhiteSpace) {
                childBlocks += CrBlock(childNode, settings)
            }
        }

        return childBlocks
    }

    override fun getIndent(): Indent? = Indent.getNoneIndent()

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        return ChildAttributes(getChildIndent(newChildIndex), null)
    }

    private fun getChildIndent(childIndex: Int): Indent? {
        val defaultIndent = Indent.getNoneIndent()

        val indentOptions = settings.getCommonSettings(CrystalLanguage).indentOptions
        val useRelativeIndent = indentOptions != null && indentOptions.USE_RELATIVE_INDENTS
        val psiParent = myNode.psi
        val subBlocks = subBlocks
        val blockCount = subBlocks.size

        if (psiParent is CrConditionalExpression) return Indent.getContinuationWithoutFirstIndent(useRelativeIndent)

        if (childIndex in 1..blockCount) {
            val prevBlock = subBlocks[childIndex - 1] as? CrBlock ?: return defaultIndent
            val prevLeaf = prevBlock.node.psi.lastSignificantLeaf()
            if (shouldUseContinuationIndent(prevLeaf)) return Indent.getContinuationIndent()
        }

        val psiPrev = (subBlocks.getOrNull(childIndex - 1) as? CrBlock)?.node?.psi
        val psiNext = (subBlocks.getOrNull(childIndex) as? CrBlock)?.node?.psi
        if (shouldUseNormalIndent(psiParent, psiPrev, psiNext)) return Indent.getNormalIndent(useRelativeIndent)

        return defaultIndent
    }

    private fun shouldUseNormalIndent(parent: PsiElement, prev: PsiElement?, next: PsiElement?) = when (parent) {
        is CrFunctionLikeDefinition, is CrDefinitionWithBody, is CrAnnotation -> true

        is CrBlockExpression -> {
            val type = parent.firstChild.elementType
            type == CR_DO || type == CR_LBRACE || type == CR_BEGIN
        }

        is CrIfExpression, is CrUnlessExpression -> {
            parent.firstChild.elementType is CrystalKeywordTokenType &&
                    (next is CrThenClause || next.elementType == CR_END)
        }

        is CrWhileExpression, is CrUntilExpression -> {
            next == null || next is CrBlockExpression || next.elementType == CR_END
        }

        is CrElseClause, is CrWhenClause -> true

        is CrCaseExpression, is CrSelectExpression -> {
            next == null ||
                    (next is CrElseClause || next is CrWhenClause || next.elementType == CR_END) && (prev is CrElseClause || prev is CrWhenClause)
        }

        else -> false
    }

    private fun shouldUseContinuationIndent(prevLeaf: PsiElement?): Boolean {
        if (prevLeaf == null) return false
        val prevType = prevLeaf.elementType

        if (prevType in CR_CONTINUATION_FORCING_TOKENS) return true

        val parent = prevLeaf.parent
        if (prevType in CR_ALL_OPERATORS && parent is CrBinaryExpression) return true
        if (prevType in CR_EXPRESSION_SUFFIX_KEYWORDS && parent.firstChild != prevLeaf) return true

        return false
    }

    companion object {
        private val CR_EXPRESSION_SUFFIX_KEYWORDS = TokenSet.create(CR_IF, CR_UNLESS, CR_RESCUE, CR_ENSURE)

        private val CR_CONTINUATION_FORCING_TOKENS = TokenSet.create(
            CR_LPAREN, CR_LBRACKET, CR_LBRACE, CR_MACRO_EXPRESSION_LBRACE, CR_MACRO_CONTROL_LBRACE,
            CR_DOT, CR_PATH_OP, CR_IN, CR_COMMA, CR_BIG_ARROW_OP, CR_INTERPOLATION_START, CR_QUESTION, CR_COLON
        )
    }
}
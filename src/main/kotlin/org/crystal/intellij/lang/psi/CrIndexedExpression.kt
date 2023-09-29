package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.crystal.intellij.lang.lexer.CR_INDEXED_OP
import org.crystal.intellij.lang.lexer.CR_LBRACKET
import org.crystal.intellij.lang.lexer.CR_QUESTION

class CrIndexedExpression(node: ASTNode) : CrCallLikeExpression(node) {
    companion object {
        private val OPERATORS = TokenSet.create(CR_LBRACKET, CR_INDEXED_OP)
    }

    override fun accept(visitor: CrVisitor) = visitor.visitIndexedExpression(this)

    override val ownReceiver: CrExpression?
        get() = childOfType()

    val operator: PsiElement
        get() = findChildByType(OPERATORS)!!

    override val argumentList: CrArgumentList?
        get() = childOfType()

    override val blockArgument: CrBlockExpression?
        get() = null

    val isNilable: Boolean
        get() = findChildByType<PsiElement>(CR_QUESTION) != null
}
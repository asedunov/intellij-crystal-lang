package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.lang.lexer.CR_QUESTION

class CrIndexedExpression(node: ASTNode) : CrCallLikeExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIndexedExpression(this)

    override val ownReceiver: CrExpression?
        get() = childOfType()

    override val argumentList: CrArgumentList?
        get() = childOfType()

    override val blockArgument: CrBlockExpression?
        get() = null

    val isNilable: Boolean
        get() = findChildByType<PsiElement>(CR_QUESTION) != null
}
package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.lang.lexer.CR_AS_QUESTION

class CrAsExpression(node: ASTNode) : CrExpressionImpl(node), CrExpressionWithReceiver {
    override fun accept(visitor: CrVisitor) = visitor.visitAsExpression(this)

    val isNilable: Boolean
        get() = findChildByType<PsiElement>(CR_AS_QUESTION) != null

    val typeElement: CrTypeElement<*>?
        get() = childOfType()
}
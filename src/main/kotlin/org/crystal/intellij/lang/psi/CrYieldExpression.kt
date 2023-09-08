package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.lang.lexer.CR_YIELD

class CrYieldExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitYieldExpression(this)

    val yieldKeyword: PsiElement?
        get() = findChildByType(CR_YIELD)

    val subject: CrExpression?
        get() = childOfType()
}
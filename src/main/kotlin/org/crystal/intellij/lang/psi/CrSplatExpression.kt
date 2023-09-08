package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

class CrSplatExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSplatExpression(this)

    val splatElement: PsiElement
        get() = firstChild

    val expression: CrExpression?
        get() = childOfType()
}
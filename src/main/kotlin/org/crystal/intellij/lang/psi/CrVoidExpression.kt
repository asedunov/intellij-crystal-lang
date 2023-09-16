package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.util.containers.JBIterable

sealed class CrVoidExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitVoidExpression(this)

    val keyword: PsiElement
        get() = firstChild

    val arguments: JBIterable<CrExpression>
        get() = childrenOfType()
}
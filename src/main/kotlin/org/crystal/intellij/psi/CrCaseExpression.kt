package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.util.containers.JBIterable

class CrCaseExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCaseExpression(this)

    val keyword: PsiElement
        get() = firstChild

    val condition: CrExpression?
        get() = childOfType()

    val whenClauses: JBIterable<CrWhenClause>
        get() = childrenOfType()

    val elseClause: CrElseClause?
        get() = childOfType()
}
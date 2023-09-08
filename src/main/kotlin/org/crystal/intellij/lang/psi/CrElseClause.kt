package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.util.containers.JBIterable

class CrElseClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitElseClause(this)

    val keyword: PsiElement
        get() = firstChild

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

class CrElseClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitElseClause(this)

    val keyword: PsiElement
        get() = firstChild
}
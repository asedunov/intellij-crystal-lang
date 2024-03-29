package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

class CrRescueClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRescueClause(this)

    val keyword: PsiElement
        get() = firstChild

    val variable: CrVariable?
        get() = childOfType()

    val type: CrTypeElement<*>?
        get() = childOfType() ?: variable?.type

    val body: CrBlockExpression?
        get() = childOfType()
}
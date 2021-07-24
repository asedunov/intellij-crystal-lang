package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_IN

class CrWhenClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitWhenClause(this)

    val keyword: PsiElement
        get() = firstChild

    val expression: CrExpression?
        get() = childOfType()

    val isExhaustive: Boolean
        get() = keyword.elementType == CR_IN
}
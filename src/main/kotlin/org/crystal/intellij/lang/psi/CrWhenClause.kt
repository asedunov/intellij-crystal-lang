package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lang.lexer.CR_IN

class CrWhenClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitWhenClause(this)

    val keyword: PsiElement
        get() = firstChild

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()

    val isExhaustive: Boolean
        get() = keyword.elementType == CR_IN
}
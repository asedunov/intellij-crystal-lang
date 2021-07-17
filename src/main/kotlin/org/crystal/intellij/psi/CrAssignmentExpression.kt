package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.lexer.CR_ASSIGN_OPERATORS

class CrAssignmentExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAssignmentExpression(this)

    val lhs: CrExpression?
        get() = childOfType()

    val operation: PsiElement
        get() = findNotNullChildByType(CR_ASSIGN_OPERATORS)

    val rhs: CrExpression?
        get() = lhs?.nextSiblingOfType()
}
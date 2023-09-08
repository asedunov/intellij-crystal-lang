package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import org.crystal.intellij.lang.lexer.CR_ASSIGN_OPERATORS
import org.crystal.intellij.lang.lexer.CR_LPAREN

class CrAssignmentExpression(node: ASTNode) : CrExpressionImpl(node), CrVisibilityHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitAssignmentExpression(this)

    val lhs: CrExpression?
        get() = childOfType()

    val operation: PsiElement
        get() = findNotNullChildByType(CR_ASSIGN_OPERATORS)

    val opSign: IElementType
        get() = operation.elementType!!

    val rhs: CrExpression?
        get() = lhs?.nextSiblingOfType()

    val isParenthesized: Boolean
        get() = findChildByType<PsiElement>(CR_LPAREN) != null
}
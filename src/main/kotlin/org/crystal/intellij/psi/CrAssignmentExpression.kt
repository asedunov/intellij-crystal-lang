package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_ASSIGN_OPERATORS

class CrAssignmentExpression(node: ASTNode) : CrExpressionImpl(node), CrVisibilityHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitAssignmentExpression(this)

    val lhs: CrExpression?
        get() = childOfType()

    val operation: PsiElement
        get() = findNotNullChildByType(CR_ASSIGN_OPERATORS)

    val opSign: IElementType
        get() = operation.elementType!!

    val rhs: CrCallArgument?
        get() = lhs?.nextSiblingOfType()
}
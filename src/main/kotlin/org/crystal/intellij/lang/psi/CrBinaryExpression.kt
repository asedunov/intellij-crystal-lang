package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import org.crystal.intellij.lang.lexer.CR_BASE_OPERATORS

class CrBinaryExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBinaryExpression(this)

    private val operator: PsiElement?
        get() = findChildByType(CR_BASE_OPERATORS)

    val opSign: IElementType?
        get() = operator?.elementType

    val leftOperand: CrExpression?
        get() = operator?.prevSiblingOfType()

    val rightOperand: CrExpression?
        get() = operator?.nextSiblingOfType()
}
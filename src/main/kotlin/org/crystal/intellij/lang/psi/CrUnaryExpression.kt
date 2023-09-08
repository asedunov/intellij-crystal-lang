package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType

class CrUnaryExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnaryExpression(this)

    private val operator: PsiElement
        get() = firstChild

    val opSign: IElementType?
        get() = operator.elementType

    val opName: String
        get() = operator.text

    val argument: CrExpression?
        get() = childOfType()
}
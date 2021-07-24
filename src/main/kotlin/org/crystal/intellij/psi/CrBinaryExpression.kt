package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_BASE_OPERATORS

class CrBinaryExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBinaryExpression(this)

    val opSign: IElementType?
        get() = findChildByType<PsiElement>(CR_BASE_OPERATORS)?.elementType
}
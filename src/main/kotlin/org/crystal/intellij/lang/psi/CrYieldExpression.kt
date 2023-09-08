package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lang.lexer.CR_YIELD
import org.crystal.intellij.lang.parser.CR_PARENTHESIZED_ARGUMENT_LIST

class CrYieldExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitYieldExpression(this)

    val yieldKeyword: PsiElement?
        get() = findChildByType(CR_YIELD)

    val subject: CrExpression?
        get() = childOfType()

    val argumentList: CrArgumentList?
        get() = childOfType()

    val hasParentheses: Boolean
        get() = argumentList?.elementType == CR_PARENTHESIZED_ARGUMENT_LIST
}
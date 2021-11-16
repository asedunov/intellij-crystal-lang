package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_PRIVATE
import org.crystal.intellij.lexer.CR_PROTECTED

class CrVisibilityExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitVisibilityExpression(this)

    val keyword: PsiElement
        get() = firstChild

    val visibility: CrVisibility?
        get() = when (firstChild.elementType) {
            CR_PRIVATE -> CrVisibility.PRIVATE
            CR_PROTECTED -> CrVisibility.PROTECTED
            else -> null
        }

    val innerExpression: CrExpression?
        get() = childOfType()
}
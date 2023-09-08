package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import org.crystal.intellij.lang.lexer.CR_COLON

class CrConditionalExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitConditionalExpression(this)

    val condition: CrExpression
        get() = childOfType()!!

    private val colon: PsiElement?
        get() = findChildByType(CR_COLON)

    val thenExpression: CrExpression?
        get() {
            for (e in condition.siblings(withSelf = false)) {
                if (e is CrExpression) return e
                if (e.elementType == CR_COLON) break
            }
            return null
        }

    val elseExpression: CrExpression?
        get() = colon?.nextSiblingOfType()
}
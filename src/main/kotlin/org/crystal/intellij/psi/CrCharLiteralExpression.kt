package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCharLiteralExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression, CrCharValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCharLiteralExpression(this)

    override val charValue: Int?
        get() {
            val leaf = childOfType<CrCharValueHolder>()
                ?: childOfType<CrUnicodeBlock>()?.childOfType<CrCharValueHolder>()
            return leaf?.charValue
        }
}
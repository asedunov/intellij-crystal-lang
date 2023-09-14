package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrCharLiteralExpression(node: ASTNode) : CrExpressionImpl(node), CrCharValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCharLiteralExpression(this)

    override val charValue: Int?
        get() {
            val leaf = childOfType<CrCharValueHolder>()
                ?: childOfType<CrUnicodeBlock>()?.childOfType<CrCharValueHolder>()
            return leaf?.charValue
        }
}
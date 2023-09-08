package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrConcatenatedStringLiteralExpression(node: ASTNode) : CrExpressionImpl(node), CrStringValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitConcatenatedStringLiteralExpression(this)

    override val stringValue: String?
        get() = buildString {
            for (literal in childrenOfType<CrStringLiteralExpression>()) {
                append(literal.stringValue ?: return null)
            }
        }
}
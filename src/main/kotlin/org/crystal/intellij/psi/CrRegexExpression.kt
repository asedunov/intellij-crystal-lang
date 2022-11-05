package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrRegexExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression {
    override fun accept(visitor: CrVisitor) = visitor.visitRegexExpression(this)
}
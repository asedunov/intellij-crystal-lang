package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNumericLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNumericLiteralExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSymbolArrayExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression {
    override fun accept(visitor: CrVisitor) = visitor.visitSymbolArrayExpression(this)
}
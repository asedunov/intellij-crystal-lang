package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrNilExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression {
    override fun accept(visitor: CrVisitor) = visitor.visitNilExpression(this)
}
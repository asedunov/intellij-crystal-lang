package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSymbolExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSymbolExpression(this)
}
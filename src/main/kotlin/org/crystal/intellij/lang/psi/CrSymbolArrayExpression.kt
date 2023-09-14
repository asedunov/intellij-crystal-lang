package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrSymbolArrayExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSymbolArrayExpression(this)
}
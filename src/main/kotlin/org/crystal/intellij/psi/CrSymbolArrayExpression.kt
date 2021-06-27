package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrSymbolArrayExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSymbolArrayExpression(this)
}
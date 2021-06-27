package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCommandExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCommandExpression(this)
}
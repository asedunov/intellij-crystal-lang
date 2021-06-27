package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSplatExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSplatExpression(this)
}
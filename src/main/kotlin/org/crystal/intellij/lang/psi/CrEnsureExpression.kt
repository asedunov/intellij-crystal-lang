package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrEnsureExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitEnsureExpression(this)
}
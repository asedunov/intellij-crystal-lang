package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNilableExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNilableExpression(this)
}
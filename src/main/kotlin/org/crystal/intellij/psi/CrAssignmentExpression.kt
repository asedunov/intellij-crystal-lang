package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAssignmentExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAssignmentExpression(this)
}
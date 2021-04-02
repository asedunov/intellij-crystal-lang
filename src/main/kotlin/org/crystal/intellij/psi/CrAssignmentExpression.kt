package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAssignmentExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAssignmentExpression(this)
}
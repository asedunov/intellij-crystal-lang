package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCaseExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCaseExpression(this)
}
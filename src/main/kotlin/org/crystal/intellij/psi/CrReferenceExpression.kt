package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrReferenceExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitReferenceExpression(this)
}
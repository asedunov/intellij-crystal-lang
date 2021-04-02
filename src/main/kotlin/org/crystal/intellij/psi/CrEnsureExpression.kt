package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrEnsureExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitEnsureExpression(this)
}
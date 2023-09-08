package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrUntilExpression(node: ASTNode) : CrLoopExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUntilExpression(this)
}
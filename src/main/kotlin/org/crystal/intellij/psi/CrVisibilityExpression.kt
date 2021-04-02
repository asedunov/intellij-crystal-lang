package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrVisibilityExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitVisibilityExpression(this)
}
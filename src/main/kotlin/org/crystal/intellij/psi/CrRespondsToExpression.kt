package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrRespondsToExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRespondsToExpression(this)
}
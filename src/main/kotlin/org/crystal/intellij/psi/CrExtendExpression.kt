package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrExtendExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitExtendExpression(this)
}
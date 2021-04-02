package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrUnaryExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnaryExpression(this)
}
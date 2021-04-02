package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrRequireExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRequireExpression(this)
}
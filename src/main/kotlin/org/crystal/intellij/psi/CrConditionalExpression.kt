package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrConditionalExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitConditionalExpression(this)
}
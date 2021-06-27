package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrHashExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHashExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrOutArgumentExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitOutArgumentExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrStringArrayExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitStringArrayExpression(this)
}
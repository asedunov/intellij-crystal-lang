package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrOffsetExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitOffsetExpression(this)
}
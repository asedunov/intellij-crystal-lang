package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrRangeExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRangeExpression(this)
}
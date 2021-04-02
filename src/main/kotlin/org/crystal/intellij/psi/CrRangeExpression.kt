package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrRangeExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRangeExpression(this)
}
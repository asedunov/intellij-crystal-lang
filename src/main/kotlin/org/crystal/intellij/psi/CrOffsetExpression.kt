package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrOffsetExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitOffsetExpression(this)
}
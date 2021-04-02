package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrUninitializedExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUninitializedExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrHeredocExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHeredocExpression(this)
}
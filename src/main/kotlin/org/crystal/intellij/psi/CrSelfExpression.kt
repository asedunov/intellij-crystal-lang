package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrSelfExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSelfExpression(this)
}
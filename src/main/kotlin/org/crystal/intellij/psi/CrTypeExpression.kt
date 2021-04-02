package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrTypeExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeExpression(this)
}
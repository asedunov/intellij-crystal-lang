package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrNilExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNilExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrExpressionType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitExpressionType(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCommandExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCommandExpression(this)
}
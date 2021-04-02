package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSplatExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSplatExpression(this)
}
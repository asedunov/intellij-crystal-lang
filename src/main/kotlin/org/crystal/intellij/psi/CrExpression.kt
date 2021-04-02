package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

abstract class CrExpression(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrFunctionPointerExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitFunctionPointerExpression(this)
}
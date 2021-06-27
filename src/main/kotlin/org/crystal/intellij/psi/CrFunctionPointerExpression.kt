package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrFunctionPointerExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitFunctionPointerExpression(this)
}
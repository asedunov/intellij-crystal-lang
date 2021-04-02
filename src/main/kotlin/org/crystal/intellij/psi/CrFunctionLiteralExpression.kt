package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrFunctionLiteralExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitFunctionLiteralExpression(this)
}
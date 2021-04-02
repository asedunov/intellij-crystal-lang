package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNumericLiteralExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNumericLiteralExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrListExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitListExpression(this)
}
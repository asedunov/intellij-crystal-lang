package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrPathExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPathExpression(this)
}
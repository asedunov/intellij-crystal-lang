package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrUnlessExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnlessExpression(this)
}
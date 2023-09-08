package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrNextExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNextExpression(this)
}
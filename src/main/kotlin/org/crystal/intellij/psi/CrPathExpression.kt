package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrPathExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPathExpression(this)
}
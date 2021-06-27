package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrArrayLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitArrayLiteralExpression(this)
}
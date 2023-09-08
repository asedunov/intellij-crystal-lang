package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrDoubleSplatExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitDoubleSplatExpression(this)

    val expression: CrExpression?
        get() = childOfType()
}
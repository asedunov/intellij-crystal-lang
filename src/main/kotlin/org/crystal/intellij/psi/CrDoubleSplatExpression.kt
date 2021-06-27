package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrDoubleSplatExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitDoubleSplatExpression(this)
}
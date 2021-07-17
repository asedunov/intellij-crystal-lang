package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAnnotationExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAnnotationExpression(this)
}
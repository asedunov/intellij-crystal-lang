package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrAnnotationExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAnnotationExpression(this)
}
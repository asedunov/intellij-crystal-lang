package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrOutArgumentExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitOutArgumentExpression(this)
}
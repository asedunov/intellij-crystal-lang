package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrBooleanLiteralExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBooleanLiteralExpression(this)
}
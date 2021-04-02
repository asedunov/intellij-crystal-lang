package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrCharLiteralExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCharLiteralExpression(this)
}
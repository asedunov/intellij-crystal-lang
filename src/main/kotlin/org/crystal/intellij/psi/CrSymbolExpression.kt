package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrSymbolExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSymbolExpression(this)
}
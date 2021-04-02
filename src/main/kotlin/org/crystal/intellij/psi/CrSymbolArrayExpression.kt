package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrSymbolArrayExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSymbolArrayExpression(this)
}
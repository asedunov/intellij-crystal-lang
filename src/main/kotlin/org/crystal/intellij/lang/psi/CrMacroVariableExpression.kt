package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrMacroVariableExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroVariableExpression(this)
}
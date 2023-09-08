package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrMacroExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroExpression(this)
}
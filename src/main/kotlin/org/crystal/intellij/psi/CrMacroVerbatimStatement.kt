package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMacroVerbatimStatement(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroVerbatimStatement(this)
}
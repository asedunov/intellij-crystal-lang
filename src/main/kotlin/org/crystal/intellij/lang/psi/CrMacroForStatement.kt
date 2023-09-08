package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrMacroForStatement(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroForStatement(this)
}
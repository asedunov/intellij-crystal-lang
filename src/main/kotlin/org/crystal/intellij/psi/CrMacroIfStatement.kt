package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMacroIfStatement(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroIfStatement(this)
}
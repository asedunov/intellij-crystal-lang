package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMacroBlockStatement(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroBlockStatement(this)
}
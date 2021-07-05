package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

sealed class CrMacroStatement(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroStatement(this)
}

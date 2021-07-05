package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMacroForStatement(node: ASTNode) : CrMacroStatement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroForStatement(this)
}
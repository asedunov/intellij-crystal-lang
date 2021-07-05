package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMacroBlockStatement(node: ASTNode) : CrMacroStatement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroBlockStatement(this)
}
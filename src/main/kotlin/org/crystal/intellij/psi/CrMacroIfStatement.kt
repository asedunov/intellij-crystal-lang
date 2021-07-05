package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMacroIfStatement(node: ASTNode) : CrMacroStatement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroIfStatement(this)
}
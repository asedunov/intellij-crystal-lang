package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMacroWrapperStatement(node: ASTNode) : CrMacroStatement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroWrapperStatement(this)
}
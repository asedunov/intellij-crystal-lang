package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMacroUnlessStatement(node: ASTNode) : CrMacroStatement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroUnlessStatement(this)
}
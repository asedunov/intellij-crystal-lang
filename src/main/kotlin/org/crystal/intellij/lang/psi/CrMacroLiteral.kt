package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrMacroLiteral(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroLiteral(this)
}
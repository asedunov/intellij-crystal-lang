package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAsmOperandList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmOperandList(this)
}
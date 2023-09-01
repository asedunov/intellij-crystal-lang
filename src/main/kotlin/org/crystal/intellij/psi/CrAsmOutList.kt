package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAsmOutList(node: ASTNode) : CrAsmOperandList(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmOutList(this)
}
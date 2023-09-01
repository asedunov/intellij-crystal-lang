package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAsmInList(node: ASTNode) : CrAsmOperandList(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmInList(this)
}
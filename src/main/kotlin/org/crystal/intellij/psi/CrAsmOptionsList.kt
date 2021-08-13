package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAsmOptionsList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmOptionsList(this)
}
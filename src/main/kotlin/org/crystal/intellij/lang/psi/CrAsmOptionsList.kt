package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrAsmOptionsList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmOptionsList(this)
}
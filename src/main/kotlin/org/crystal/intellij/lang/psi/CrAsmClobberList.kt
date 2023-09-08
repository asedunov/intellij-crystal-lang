package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrAsmClobberList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmClobberList(this)
}
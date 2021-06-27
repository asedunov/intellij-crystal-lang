package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrHashEntry(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHashEntry(this)
}
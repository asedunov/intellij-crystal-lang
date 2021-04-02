package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrInstantiatedType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitInstantiatedType(this)
}
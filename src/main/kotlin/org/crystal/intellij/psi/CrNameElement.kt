package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

sealed class CrNameElement(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNameElement(this)

    abstract override fun getName(): String?
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

sealed class CrType(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitType(this)
}
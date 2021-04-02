package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSplatType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSplatType(this)
}
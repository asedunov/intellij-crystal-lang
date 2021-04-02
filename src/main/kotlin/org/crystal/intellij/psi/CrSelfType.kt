package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSelfType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSelfType(this)
}
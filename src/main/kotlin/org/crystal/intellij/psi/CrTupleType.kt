package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrTupleType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTupleType(this)
}
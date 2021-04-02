package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNamedTupleType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedTupleType(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNilableType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNilableType(this)
}
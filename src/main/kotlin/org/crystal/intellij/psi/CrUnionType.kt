package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrUnionType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnionType(this)
}
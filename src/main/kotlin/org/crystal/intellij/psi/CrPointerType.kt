package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrPointerType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPointerType(this)
}
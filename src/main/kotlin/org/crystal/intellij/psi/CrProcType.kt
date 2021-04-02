package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrProcType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitProcType(this)
}
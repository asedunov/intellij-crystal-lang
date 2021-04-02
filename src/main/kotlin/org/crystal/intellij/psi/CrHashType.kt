package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrHashType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHashType(this)
}
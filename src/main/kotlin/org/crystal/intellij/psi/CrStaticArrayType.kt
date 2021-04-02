package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrStaticArrayType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitStaticArrayType(this)
}
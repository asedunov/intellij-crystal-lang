package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrDoubleSplatType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitDoubleSplatType(this)
}
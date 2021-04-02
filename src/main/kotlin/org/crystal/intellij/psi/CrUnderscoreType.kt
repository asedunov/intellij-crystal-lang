package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrUnderscoreType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnderscoreType(this)
}
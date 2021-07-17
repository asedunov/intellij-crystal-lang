package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSupertypeClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSupertypeClause(this)
}
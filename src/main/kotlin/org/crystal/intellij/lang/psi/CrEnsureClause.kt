package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrEnsureClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitEnsureClause(this)
}
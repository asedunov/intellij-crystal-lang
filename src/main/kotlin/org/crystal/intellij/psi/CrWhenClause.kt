package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrWhenClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitWhenClause(this)
}
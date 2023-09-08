package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrThenClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitThenClause(this)
}
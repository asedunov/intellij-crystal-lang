package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrElseClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitElseClause(this)
}
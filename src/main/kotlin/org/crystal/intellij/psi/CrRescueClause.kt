package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrRescueClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRescueClause(this)
}
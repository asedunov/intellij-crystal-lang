package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrRescueClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRescueClause(this)
}
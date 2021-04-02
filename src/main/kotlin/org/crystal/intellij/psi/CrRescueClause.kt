package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrRescueClause(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRescueClause(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrSupertypeClause(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSupertypeClause(this)
}
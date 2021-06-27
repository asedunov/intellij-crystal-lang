package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrEnsureClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitEnsureClause(this)
}
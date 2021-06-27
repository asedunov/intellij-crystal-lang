package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrWhenClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitWhenClause(this)
}
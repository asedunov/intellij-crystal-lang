package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrBody(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBodyClause(this)
}
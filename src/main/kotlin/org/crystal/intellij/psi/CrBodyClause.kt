package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrBodyClause(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBodyClause(this)
}
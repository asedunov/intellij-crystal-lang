package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrThenClause(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitThenClause(this)
}
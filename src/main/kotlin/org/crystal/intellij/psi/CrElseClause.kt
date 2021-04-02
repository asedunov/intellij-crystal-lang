package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrElseClause(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitElseClause(this)
}
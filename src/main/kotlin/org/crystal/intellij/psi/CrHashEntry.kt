package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrHashEntry(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHashEntry(this)
}
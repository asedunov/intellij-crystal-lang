package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrArgumentList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitArgumentList(this)
}
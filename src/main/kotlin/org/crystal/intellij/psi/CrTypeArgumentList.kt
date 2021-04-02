package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrTypeArgumentList(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeArgumentList(this)
}
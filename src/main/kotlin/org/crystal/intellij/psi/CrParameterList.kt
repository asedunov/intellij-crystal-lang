package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrParameterList(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitParameterList(this)
}
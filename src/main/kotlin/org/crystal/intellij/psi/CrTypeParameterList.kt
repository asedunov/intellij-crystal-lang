package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrTypeParameterList(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeParameterList(this)
}
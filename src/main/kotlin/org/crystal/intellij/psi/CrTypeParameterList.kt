package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrTypeParameterList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeParameterList(this)
}
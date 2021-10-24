package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrBody(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBody(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrTypeBody(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeBody(this)
}
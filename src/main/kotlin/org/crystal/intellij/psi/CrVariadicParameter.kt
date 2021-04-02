package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrVariadicParameter(node: ASTNode) : CrParameter(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitVariadicParameter(this)
}
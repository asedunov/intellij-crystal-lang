package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSimpleParameter(node: ASTNode) : CrParameter(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSimpleParameter(this)
}
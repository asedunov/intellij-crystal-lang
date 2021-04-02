package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

sealed class CrParameter(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitParameter(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

sealed class CrParameter(node: ASTNode) : CrDefinitionImpl(node), CrSimpleNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitParameter(this)
}
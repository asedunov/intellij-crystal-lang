package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrTypeDef(node: ASTNode) : CrDefinitionImpl(node), CrAliasLikeDefinition, CrSimpleNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeDef(this)
}
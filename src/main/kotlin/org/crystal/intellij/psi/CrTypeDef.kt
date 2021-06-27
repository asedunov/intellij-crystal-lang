package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrTypeDef(node: ASTNode) : CrDefinitionImpl(node), CrAliasLikeDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeDef(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAlias(node: ASTNode) : CrDefinitionImpl(node), CrPathBasedDefinition, CrAliasLikeDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitAlias(this)
}
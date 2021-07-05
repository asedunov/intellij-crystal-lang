package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrTypeParameter(node: ASTNode) : CrDefinitionImpl(node), CrTypeDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeParameter(this)
}
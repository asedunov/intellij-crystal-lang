package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrClass(node: ASTNode) : CrDefinitionImpl(node), CrTypeDefinitionWithBody, CrPathBasedDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitClass(this)
}
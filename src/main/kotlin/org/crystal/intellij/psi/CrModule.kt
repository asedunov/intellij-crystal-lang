package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrModule(node: ASTNode) : CrDefinitionImpl(node), CrTypeDefinitionWithBody, CrPathBasedDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitModule(this)
}
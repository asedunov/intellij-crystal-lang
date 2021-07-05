package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrClass(node: ASTNode) : CrDefinitionImpl(node),
                               CrBodyHolder,
                               CrPathBasedDefinition,
                               CrTypeDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitClass(this)
}
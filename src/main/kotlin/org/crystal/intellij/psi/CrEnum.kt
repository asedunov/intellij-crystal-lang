package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrEnum(node: ASTNode) : CrDefinitionImpl(node),
                              CrPathBasedDefinition,
                              CrBodyHolder,
                              CrTypeDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitEnum(this)
}
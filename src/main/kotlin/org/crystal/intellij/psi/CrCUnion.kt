package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCUnion(node: ASTNode) : CrDefinitionImpl(node),
                                CrBodyHolder,
                                CrTypeDefinition,
                                CrSimpleNameElementHolder,
                                CrDefinitionWithFqName {
    override fun accept(visitor: CrVisitor) = visitor.visitCUnion(this)
}
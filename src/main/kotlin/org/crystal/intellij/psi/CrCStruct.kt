package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCStruct(node: ASTNode) : CrDefinitionImpl(node),
                                 CrBodyHolder,
                                 CrTypeDefinition,
                                 CrSimpleNameElementHolder,
                                 CrDefinitionWithFqName {
    override fun accept(visitor: CrVisitor) = visitor.visitCStruct(this)
}